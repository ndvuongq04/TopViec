import { ref, onMounted, onUnmounted } from 'vue';

export function useScrollDirection() {
  const isScrollingDown = ref(false);
  const lastScrollY = ref(0);
  const isScrolledPastHeader = ref(false);

  function handleScroll() {
    const currentScrollY = window.scrollY;
    
    // Determine scroll direction
    if (currentScrollY > lastScrollY.value && currentScrollY > 65) {
      isScrollingDown.value = true;
    } else if (currentScrollY < lastScrollY.value) {
      isScrollingDown.value = false;
    }
    
    // Check if scrolled past header
    isScrolledPastHeader.value = currentScrollY > 65;
    
    lastScrollY.value = currentScrollY;
  }

  onMounted(() => {
    window.addEventListener('scroll', handleScroll, { passive: true });
    handleScroll();
  });

  onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll);
  });

  return {
    isScrollingDown,
    isScrolledPastHeader
  };
}
