<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
  <v-layout
    row
    align-center
    justify-center
    fill-height
  >
    <v-card light>
      <v-card-title class="display-1 justify-center">Projects</v-card-title>
      <v-divider></v-divider>
      <v-list
        style="max-height: 600px; width: 500px"
        class="scroll-y"
      >
        <template
          v-for="item in 20"
        >
          <v-list-tile
            :key="item"
            avatar
            @click=""
          >
            <v-list-tile-content
              @click="chooseProject(item)">
              <v-list-tile-title v-text="item"></v-list-tile-title>
            </v-list-tile-content>

            <v-btn
              icon
            >
              <v-icon>settings</v-icon>
            </v-btn>
          </v-list-tile>
        </template>
      </v-list>
      <v-divider></v-divider>
      <v-card-actions class="justify-center">
        <v-dialog v-model="dialog" persistent max-width="600px">
          <template v-slot:activator="{ on }">
            <v-btn
              color="blue darken-1"
              flat
              v-on="on"
            >
              <v-icon dark>add</v-icon>
              Create
            </v-btn>
          </template>
          <v-card>
            <v-card-title>
              <span class="headline">User Profile</span>
            </v-card-title>
            <v-card-text>
              <v-container grid-list-md>
                <v-layout wrap>
                  <v-flex xs12 sm6 md4>
                    <v-text-field label="Legal first name*" required></v-text-field>
                  </v-flex>
                  <v-flex xs12 sm6 md4>
                    <v-text-field label="Legal middle name" hint="example of helper text only on focus"></v-text-field>
                  </v-flex>
                  <v-flex xs12 sm6 md4>
                    <v-text-field
                      label="Legal last name*"
                      hint="example of persistent helper text"
                      persistent-hint
                      required
                    ></v-text-field>
                  </v-flex>
                  <v-flex xs12>
                    <v-text-field label="Email*" required></v-text-field>
                  </v-flex>
                  <v-flex xs12>
                    <v-text-field label="Password*" type="password" required></v-text-field>
                  </v-flex>
                  <v-flex xs12 sm6>
                    <v-select
                      :items="['0-17', '18-29', '30-54', '54+']"
                      label="Age*"
                      required
                    ></v-select>
                  </v-flex>
                  <v-flex xs12 sm6>
                    <v-autocomplete
                      :items="['Skiing', 'Ice hockey', 'Soccer', 'Basketball', 'Hockey', 'Reading', 'Writing', 'Coding', 'Basejump']"
                      label="Interests"
                      multiple
                    ></v-autocomplete>
                  </v-flex>
                </v-layout>
              </v-container>
              <small>*indicates required field</small>
            </v-card-text>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" flat @click="dialog = false">Close</v-btn>
              <v-btn color="blue darken-1" flat @click="dialog = false">Save</v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-card-actions>
    </v-card>
  </v-layout>
</template>

<script>
import axios from 'axios'
import router from '@/router'

export default {
  name: 'Main',
  data () {
    return {
      results: null,
      dialog: false
    }
  },
  mounted () {
    axios.get('http://localhost:8090/getAllProjects')
      .then(response => {
        this.results = response.data
      })
      .catch(error => console.log(error))
  },
  methods: {
    chooseProject (name) {
      router.push('project/' + name)
    }
  }
}
</script>

<style scoped>
</style>
