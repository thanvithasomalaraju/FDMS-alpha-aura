(function () {
  'use strict';
  document.addEventListener('DOMContentLoaded', () => {
    // Optional: fetch public stats
    fetch('/api/public/summary')
      .then(r => r.json())
      .then(json => {
        if (json && json.success && json.data) {
          const nums = document.querySelectorAll('.about-stats .stat .number');
          if (nums && nums.length >= 4) {
            nums[0].textContent = json.data.partnerRestaurants || nums[0].textContent;
            nums[1].textContent = json.data.deliveryPartners || nums[1].textContent;
            nums[2].textContent = json.data.customers || nums[2].textContent;
            nums[3].textContent = json.data.avgRating || nums[3].textContent;
          }
        }
      }).catch(()=>{/* keep static numbers */});
  });
})();
