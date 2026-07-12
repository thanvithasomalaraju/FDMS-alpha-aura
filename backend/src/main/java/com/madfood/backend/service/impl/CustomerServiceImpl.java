package com.madfood.backend.service.impl;

import com.madfood.backend.dto.*;
import com.madfood.backend.entity.*;
import com.madfood.backend.exception.BadRequestException;
import com.madfood.backend.exception.ResourceNotFoundException;
import com.madfood.backend.mapper.*;
import com.madfood.backend.repository.*;
import com.madfood.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public CustomerProfileDto getProfile(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        return CustomerMapper.toCustomerProfileDto(customer);
    }

    @Override
    @Transactional
    public CustomerProfileDto updateProfile(Long customerId, CustomerProfileDto profileDto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        
        customer.setName(profileDto.getName());
        customer.setPhone(profileDto.getPhone());
        if (profileDto.getProfileImage() != null) {
            customer.setProfileImage(profileDto.getProfileImage());
        }
        
        customerRepository.save(customer);
        return CustomerMapper.toCustomerProfileDto(customer);
    }

    @Override
    @Transactional
    public AddressDto addAddress(Long customerId, AddressDto addressDto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Address address = Address.builder()
                .customer(customer)
                .addressLine(addressDto.getAddressLine())
                .latitude(addressDto.getLatitude())
                .longitude(addressDto.getLongitude())
                .build();
        addressRepository.save(address);
        return CustomerMapper.toAddressDto(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long customerId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        if (!address.getCustomer().getId().equals(customerId)) {
            throw new BadRequestException("Address does not belong to the customer!");
        }
        addressRepository.delete(address);
    }

    @Override
    public List<RestaurantDto> getApprovedRestaurants() {
        return restaurantRepository.findByStatus("APPROVED").stream()
                .map(RestaurantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodItemDto> getMenuItems(Long restaurantId) {
        return foodItemRepository.findByRestaurantIdAndIsAvailable(restaurantId, true).stream()
                .map(FoodMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodItemDto> searchFood(String query) {
        return foodItemRepository.findByNameContainingIgnoreCase(query).stream()
                .map(FoodMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartDto getCart(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> cartRepository.save(Cart.builder().customer(customer).build()));
        return CartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto addToCart(Long customerId, Long foodItemId, Integer quantity) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> cartRepository.save(Cart.builder().customer(customer).build()));

        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));

        CartItem cartItem = cartItemRepository.findByCartIdAndFoodItemId(cart.getId(), foodItemId)
                .orElse(null);

        if (cartItem == null) {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .foodItem(foodItem)
                    .quantity(quantity)
                    .build();
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartItemRepository.save(cartItem);
        
        return getCart(customerId);
    }

    @Override
    @Transactional
    public CartDto updateCartQuantity(Long customerId, Long foodItemId, Integer quantity) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository.findByCartIdAndFoodItemId(cart.getId(), foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
        return getCart(customerId);
    }

    @Override
    @Transactional
    public CartDto removeFromCart(Long customerId, Long foodItemId) {
        return updateCartQuantity(customerId, foodItemId, 0);
    }

    @Override
    @Transactional
    public OrderDto placeOrder(Long customerId, String paymentMethod, Long addressId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("No items in cart to place order");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Select a delivery address"));

        // Determine Restaurant (assume all items are from same restaurant for order constraints)
        Restaurant restaurant = cart.getCartItems().get(0).getFoodItem().getRestaurant();

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        
        Order order = Order.builder()
                .customer(customer)
                .restaurant(restaurant)
                .status("new")
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentMethod.equalsIgnoreCase("COD") ? "Pending" : "Paid")
                .deliveryAddress(address.getAddressLine())
                .totalAmount(0.0)
                .build();
        
        orderRepository.save(order);

        for (CartItem ci : cart.getCartItems()) {
            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .foodItem(ci.getFoodItem())
                    .quantity(ci.getQuantity())
                    .price(ci.getFoodItem().getPrice())
                    .build();
            orderItemRepository.save(oi);
            orderItems.add(oi);
            totalAmount += oi.getPrice() * oi.getQuantity();
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        // Empty Cart
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);

        // Generate Transaction
        String txnId = "TXN-" + (1000 + UUID.randomUUID().hashCode() % 8999);
        Payment payment = Payment.builder()
                .order(order)
                .transactionId(txnId)
                .paymentMethod(paymentMethod)
                .amount(totalAmount)
                .paymentStatus(paymentMethod.equalsIgnoreCase("COD") ? "Pending" : "Success")
                .build();
        paymentRepository.save(payment);

        // Notifications
        notificationRepository.save(Notification.builder()
                .user(customer.getUser())
                .title("Order Placed")
                .message("Your order " + order.getId() + " is placed successfully with " + restaurant.getName())
                .build());

        notificationRepository.save(Notification.builder()
                .user(restaurant.getUser())
                .title("New Order")
                .message("You received a new order " + order.getId() + " from " + customer.getName())
                .build());

        return OrderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getOrderHistory(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return OrderMapper.toDto(order);
    }

    @Override
    @Transactional
    public ReviewDto addReview(Long customerId, Long restaurantId, ReviewDto reviewDto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Review review = Review.builder()
                .customer(customer)
                .restaurant(restaurant)
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment() != null ? reviewDto.getComment() : reviewDto.getReview())
                .status("Published")
                .build();
        reviewRepository.save(review);
        return ReviewMapper.toDto(review);
    }

    @Override
    public List<ReviewDto> getReviews(Long restaurantId) {
        return reviewRepository.findByRestaurantIdAndStatus(restaurantId, "Published").stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDto> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(n -> NotificationDto.builder()
                        .id("N" + (n.getId() + 100))
                        .numericId(n.getId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .status(n.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
