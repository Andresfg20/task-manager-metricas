import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// In development the SPA runs on 5173 and proxies /api to the backend on 8080.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: './src/setupTests.js',
  },
});
