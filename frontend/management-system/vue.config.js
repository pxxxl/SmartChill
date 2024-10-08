const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  
  devServer: {
    client: {
      overlay: false
    },
    proxy: {
      '/api': {
        target: 'http://731452ee.r7.cpolar.top',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        },
      }
      
    }
  }
})
