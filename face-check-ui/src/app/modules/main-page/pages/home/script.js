document.addEventListener('DOMContentLoaded', function() {
  const swiper = new Swiper('.swiper', {
    // Enable navigation arrows
    navigation: {
      nextEl: '.swiper-button-next',
      prevEl: '.swiper-button-prev',
    },
    // Enable pagination
    pagination: {
      el: '.swiper-pagination',
      clickable: true,
    },
    // Configure number of slides to show
    slidesPerView: 3,
    spaceBetween: 20,
    // Responsive breakpoints
    breakpoints: {
      // when window width is >= 1024px
      1024: {
        slidesPerView: 3,
        spaceBetween: 20
      },
      // when window width is >= 768px
      768: {
        slidesPerView: 2,
        spaceBetween: 15
      },
      // when window width is < 768px
      0: {
        slidesPerView: 1,
        spaceBetween: 10
      }
    },
    // Loop through slides
    loop: true,
    // Speed
    speed: 500,
    // Disable auto height as we'll use fixed heights
    autoHeight: false,
  });
});
