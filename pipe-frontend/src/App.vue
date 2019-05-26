<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <v-app dark class="grey lighten-2">
    <div id="app">
      <v-toolbar app>
        <v-toolbar-side-icon></v-toolbar-side-icon>

        <v-toolbar-title
          class="name"
          @click="navigateToMain"
        >
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <span v-on="on">( Pipe )</span>
            </template>
            <span>Return to the Main page</span>
          </v-tooltip>
        </v-toolbar-title>

        <v-spacer></v-spacer>

        <v-btn icon>
          <v-icon>search</v-icon>
        </v-btn>
        <v-btn icon>
          <v-icon>refresh</v-icon>
        </v-btn>
        <v-btn icon>
          <v-icon>more_vert</v-icon>
        </v-btn>
      </v-toolbar>

      <v-parallax
        src="https://img.wallpapersafari.com/desktop/1920/1080/84/39/jZltcL.jpg"
        height="auto"
      >
        <router-view/>
      </v-parallax>

      <v-footer
        app
        height="auto"
      >
        <v-layout
          justify-center
          row
          wrap
        >
          <v-flex
            grey
            darken-2
            py-3
            text-xs-center
            white--text
            xs12
          >
            &copy; 2019 — <strong>Interminable, Inc.</strong> All Rights Reserved.
          </v-flex>
        </v-layout>
      </v-footer>
    </div>
  </v-app>
</template>

<script>
import router from './router'

export default {
  name: 'App',
  methods: {
    navigateToMain () {
      router.push('/')
    }
  }
}
</script>

<style>
  @font-face {
    font-family: 'Great Vibes';
    src: url('./fonts/great-vibes.regular.ttf');
    font-weight: normal;
    font-style: normal;
  }

  #app {
    font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Oxygen, Ubuntu, Cantarell, Fira Sans, Droid Sans, Helvetica Neue, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
  }

  .name {
    color: ghostwhite;
    font-family: "Great Vibes", cursive;
    font-size: 48px;
    text-shadow: 1px 1px 1px #ccc;
  }

</style>
