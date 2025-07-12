import { saveAs } from 'file-saver'

/**
 * 格式化图片大小
 * @param size
 */
export const formatSize = (size?: number) => {
  if (!size) return '未知'
  if (size < 1024) return '未知'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + 'KB'
  return (size / (1024 * 1024)).toFixed(2) + 'MB'
}

/**
 * 下载图片
 * @param url 图片下载地址
 * @param fileName 要保存的文件名
 */
export function downloadImage(url?: string, fileName?: string) {
  if(!url){
    return
  }
  saveAs(url, fileName)
}
