import { defineConfig } from 'vite';

export default defineConfig({
  resolve: {
    alias: {
      'rxjs': 'rxjs/dist/esm/index.js',
    },
  },
  optimizeDeps: {
    include: ['rxjs']
  }
});