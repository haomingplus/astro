/**
 * 文件上传相关 API
 *
 * 包含图片上传到OSS等接口
 */
import request from '@/utils/request'

export const uploadApi = {
  /**
   * 上传图片到OSS
   *
   * @param {FormData} formData - 包含图片文件的表单数据
   * @returns {Promise} 上传结果，包含图片URL
   */
  uploadImage(formData) {
    return request.post('/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 批量上传图片到OSS
   *
   * @param {FormData} formData - 包含多个图片文件的表单数据
   * @returns {Promise} 上传结果，包含图片URL列表
   */
  uploadImages(formData) {
    return request.post('/upload/images', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
