import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path';// 確保有引入 path

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@components': path.resolve(__dirname, './src/components'),
      '@common': path.resolve(__dirname, './src/components/common'),
      '@domain': path.resolve(__dirname, './src/components/domain'),
      '@config': path.resolve(__dirname, './src/config'),
      '@constants': path.resolve(__dirname, './src/constants'),
    }
  },
  server: {
    port: 5173,
    host: '0.0.0.0', // 關鍵：強制 Vite 監聽所有網路介面（包含 Google 內部的 10.88.x.x）
    allowedHosts: true, // 允許 Google Cloud Shell 的隨機域名存取
    strictPort: true
  }
})