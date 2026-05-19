import { useAuthStore } from "@/stores/auth.store";
import { useCvsStore } from "@/stores/cvs.store";
import { useApplicationStore } from "@/stores/application.store";
import { useToast } from "@/composables/useToast";

export function useQuickApply() {
  const authStore = useAuthStore();
  const cvsStore = useCvsStore();
  const applicationStore = useApplicationStore();
  const toast = useToast();

  const handleQuickApply = async (jobId: number | string, jobTitle: string) => {
    if (!authStore.isAuthenticated) {
      toast.info("Vui lòng đăng nhập để ứng tuyển nhanh");
      return false;
    }

    try {
      if (cvsStore.cvs.length === 0) {
        await cvsStore.fetchMyCvs();
      }

      const defaultCv = cvsStore.cvs.find(cv => cv.isDefault);
      
      if (!defaultCv) {
        toast.warning(
          "Thiếu CV mặc định", 
          "Bạn cần thiết lập một CV làm mặc định để sử dụng chức năng ứng tuyển nhanh."
        );
        return false;
      }

      // Real application call
      const numericJobId = typeof jobId === 'string' ? parseInt(jobId) : jobId;
      await applicationStore.quickApply(numericJobId);
      
      toast.success(
        "Ứng tuyển nhanh thành công!", 
        `Đã nộp hồ sơ "${defaultCv.title}" cho vị trí ${jobTitle}.`
      );
      
      return true;
    } catch (err: any) {
      // Check for specific error like "Already applied" if backend provides it
      const message = err?.response?.data?.message || "Không thể thực hiện ứng tuyển nhanh. Vui lòng thử lại.";
      toast.error("Lỗi", message);
      return false;
    }
  };

  return {
    handleQuickApply
  };
}
