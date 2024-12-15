import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173, // Specify the port
    open: true, // Automatically open the app in the browser
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/setupTests.js',
    coverage: {
      provider: 'c8', // Use c8 for coverage
      reportsDirectory: './coverage',
    },
  },
});
