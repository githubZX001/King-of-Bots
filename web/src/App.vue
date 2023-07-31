<template>
  
  <Navbar/>
  <router-view/>
</template>
<script>

import Navbar from '@/components/NarBar.vue';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap";

export default {
   components:{
    Navbar
   },
   setup() {
    
   }

}
</script>

<style>
  body{
    background-image: url("@/assets/images/backend.png");
    background-size: cover;
  }
</style>
