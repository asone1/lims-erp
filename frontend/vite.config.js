import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: '0.0.0.0', // 關鍵：強制 Vite 監聽所有網路介面（包含 Google 內部的 10.88.x.x）
    allowedHosts: true, // 允許 Google Cloud Shell 的隨機域名存取
    strictPort: true
  }
})