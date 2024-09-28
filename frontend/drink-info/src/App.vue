<template>
  <div id="app">
    <mainPage />
  </div>
</template>

<script>
import mainPage from './components/main-page.vue'

export default {
  name: 'App',
  components: {
    mainPage,
  }
}
</script>

<style>

*{
  margin: 0;
  padding: 0;
}
html{
  background-color:#f5f5f5;
  height: 100vh;
}
</style>
