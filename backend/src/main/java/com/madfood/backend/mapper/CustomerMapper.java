package com.madfood.backend.mapper;

import com.madfood.backend.dto.AddressDto;
import com.madfood.backend.dto.CustomerProfileDto;
import com.madfood.backend.entity.Address;
import com.madfood.backend.entity.Customer;
import java.util.stream.Collectors;

public class CustomerMapper {
    public static AddressDto toAddressDto(Address address) {
        if (address == null) return null;
        return AddressDto.builder()
                .id(address.getId())
                .addressLine(address.getAddressLine())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }

    public static CustomerProfileDto toCustomerProfileDto(Customer customer) {
        if (customer == null) return null;
        return CustomerProfileDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getUser().getEmail())
                .phone(customer.getPhone())
                .profileImage(customer.getProfileImage())
                .addresses(customer.getAddresses() != null ? 
                        customer.getAddresses().stream().map(CustomerMapper::toAddressDto).collect(Collectors.toList()) : null)
                .build();
    }
}
