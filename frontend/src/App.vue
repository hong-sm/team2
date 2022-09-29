

<template>
    <v-app id="inspire">
        <div>
            <v-app-bar app clipped-left flat>
                <v-toolbar-title>
                    <span class="second-word font uppercase"
                        style="font-weight:700;"
                    >
                        <v-app-bar-nav-icon
                            @click="openSideBar()"
                            style="z-index:1;
                            height:56px;
                            width:30px;
                            margin-right:10px;
                            font-weight:300;
                            font-size:55px;"
                        >
                            <div style="line-height:100%;">≡</div>
                        </v-app-bar-nav-icon>
                        team
                    </span>
                </v-toolbar-title>
                <span v-if="urlPath!=null" 
                    class="mdi mdi-home" 
                    key="" 
                    to="/" 
                    @click="goHome()"
                    style="margin-left:10px; font-size:20px; cursor:pointer;"
                    ></span> 
                <v-spacer></v-spacer>

            </v-app-bar>

            <v-navigation-drawer app clipped flat v-model="sideBar">
                <v-list>


                    <v-list-item
                        class="px-2"
                        key="requests"
                        to="/requests"
                        @click="changeUrl()"
                        color="deep-purple lighten-2"
                        style="font-weight:700;"
                    >
                        Request
                    </v-list-item>


                    <v-list-item
                        class="px-2"
                        key="readServiceLists"
                        to="readServiceLists"
                        @click="changeUrl()"
                        color="deep-purple lighten-2"
                        style="font-weight:700;"
                    >
                        ReadServiceList
                    </v-list-item>
                    <v-list-item
                        class="px-2"
                        key="readRequests"
                        to="readRequests"
                        @click="changeUrl()"
                        color="deep-purple lighten-2"
                        style="font-weight:700;"
                    >
                        ReadRequest
                    </v-list-item>

                    <v-list-item
                        class="px-2"
                        key="services"
                        to="/services"
                        @click="changeUrl()"
                        color="deep-purple lighten-2"
                        style="font-weight:700;"
                    >
                        Service
                    </v-list-item>



                    <v-list-item
                        class="px-2"
                        key="stocks"
                        to="/stocks"
                        @click="changeUrl()"
                        color="deep-purple lighten-2"
                        style="font-weight:700;"
                    >
                        Stock
                    </v-list-item>



                    <v-list-item
                        class="px-2"
                        key="pays"
                        to="/pays"
                        @click="changeUrl()"
                        color="deep-purple lighten-2"
                        style="font-weight:700;"
                    >
                        Pay
                    </v-list-item>



                    <v-list-item
                        class="px-2"
                        key="progressViews"
                        to="progressViews"
                        @click="changeUrl()"
                        color="deep-purple lighten-2"
                        style="font-weight:700;"
                    >
                        ProgressView
                    </v-list-item>


                </v-list>
            </v-navigation-drawer>
        </div>

        <v-main>
            <v-container v-if="urlPath" class="py-8 px-6 mt-10" fluid>
                <router-view></router-view>
            </v-container>
            <v-container v-else class="py-8 px-6 mt-10" fluid>
                <v-row>
                        <v-card
                            class="mx-auto"
                            style="height:300px; width:300px; margin-bottom:20px;"
                            outlined
                        >
                            <v-list-item>
                                <v-list-item-avatar 
                                    class="mx-auto"
                                    size="80"
                                    style="margin-top:80px;"
                                ><span class="mdi mdi-apps" style="font-size:60px; color:#9575CD;"></span>
                                </v-list-item-avatar>
                            </v-list-item>

                            <v-card-actions>
                                <v-btn 
                                    class="mx-auto"
                                    outlined
                                    rounded
                                    key="requests"
                                    to="/requests"
                                    @click="changeUrl()"
                                    color="deep-purple lighten-2"
                                    style="font-weight:500; font-size:20px; padding:15px; border:solid 2px;"
                                >
                                    Request
                                </v-btn>
                            </v-card-actions>
                        </v-card>

                        <v-card
                            class="mx-auto"
                            style="height:300px; width:300px; margin-bottom:20px;"
                            outlined
                        >
                            <v-list-item>
                                <v-list-item-avatar 
                                    class="mx-auto"
                                    size="80"
                                    style="margin-top:80px;"
                                ><span class="mdi mdi-apps" style="font-size:60px; color:#9575CD;"></span>
                                </v-list-item-avatar>
                            </v-list-item>

                            <v-card-actions>
                                <v-btn 
                                    class="mx-auto"
                                    outlined
                                    rounded
                                    key="readServiceLists"
                                    to="/readServiceLists"
                                    @click="changeUrl()"
                                    color="deep-purple lighten-2"
                                    style="font-weight:500; font-size:20px; padding:15px;"
                                >
                                    ReadServiceList
                                </v-btn>
                            </v-card-actions>
                        </v-card>
                        <v-card
                            class="mx-auto"
                            style="height:300px; width:300px; margin-bottom:20px;"
                            outlined
                        >
                            <v-list-item>
                                <v-list-item-avatar 
                                    class="mx-auto"
                                    size="80"
                                    style="margin-top:80px;"
                                ><span class="mdi mdi-apps" style="font-size:60px; color:#9575CD;"></span>
                                </v-list-item-avatar>
                            </v-list-item>

                            <v-card-actions>
                                <v-btn 
                                    class="mx-auto"
                                    outlined
                                    rounded
                                    key="readRequests"
                                    to="/readRequests"
                                    @click="changeUrl()"
                                    color="deep-purple lighten-2"
                                    style="font-weight:500; font-size:20px; padding:15px;"
                                >
                                    ReadRequest
                                </v-btn>
                            </v-card-actions>
                        </v-card>
                        <v-card
                            class="mx-auto"
                            style="height:300px; width:300px; margin-bottom:20px;"
                            outlined
                        >
                            <v-list-item>
                                <v-list-item-avatar 
                                    class="mx-auto"
                                    size="80"
                                    style="margin-top:80px;"
                                ><span class="mdi mdi-apps" style="font-size:60px; color:#9575CD;"></span>
                                </v-list-item-avatar>
                            </v-list-item>

                            <v-card-actions>
                                <v-btn 
                                    class="mx-auto"
                                    outlined
                                    rounded
                                    key="services"
                                    to="/services"
                                    @click="changeUrl()"
                                    color="deep-purple lighten-2"
                                    style="font-weight:500; font-size:20px; padding:15px; border:solid 2px;"
                                >
                                    Service
                                </v-btn>
                            </v-card-actions>
                        </v-card>

                        <v-card
                            class="mx-auto"
                            style="height:300px; width:300px; margin-bottom:20px;"
                            outlined
                        >
                            <v-list-item>
                                <v-list-item-avatar 
                                    class="mx-auto"
                                    size="80"
                                    style="margin-top:80px;"
                                ><span class="mdi mdi-apps" style="font-size:60px; color:#9575CD;"></span>
                                </v-list-item-avatar>
                            </v-list-item>

                            <v-card-actions>
                                <v-btn 
                                    class="mx-auto"
                                    outlined
                                    rounded
                                    key="stocks"
                                    to="/stocks"
                                    @click="changeUrl()"
                                    color="deep-purple lighten-2"
                                    style="font-weight:500; font-size:20px; padding:15px; border:solid 2px;"
                                >
                                    Stock
                                </v-btn>
                            </v-card-actions>
                        </v-card>

                        <v-card
                            class="mx-auto"
                            style="height:300px; width:300px; margin-bottom:20px;"
                            outlined
                        >
                            <v-list-item>
                                <v-list-item-avatar 
                                    class="mx-auto"
                                    size="80"
                                    style="margin-top:80px;"
                                ><span class="mdi mdi-apps" style="font-size:60px; color:#9575CD;"></span>
                                </v-list-item-avatar>
                            </v-list-item>

                            <v-card-actions>
                                <v-btn 
                                    class="mx-auto"
                                    outlined
                                    rounded
                                    key="pays"
                                    to="/pays"
                                    @click="changeUrl()"
                                    color="deep-purple lighten-2"
                                    style="font-weight:500; font-size:20px; padding:15px; border:solid 2px;"
                                >
                                    Pay
                                </v-btn>
                            </v-card-actions>
                        </v-card>


                        <v-card
                            class="mx-auto"
                            style="height:300px; width:300px; margin-bottom:20px;"
                            outlined
                        >
                            <v-list-item>
                                <v-list-item-avatar 
                                    class="mx-auto"
                                    size="80"
                                    style="margin-top:80px;"
                                ><span class="mdi mdi-apps" style="font-size:60px; color:#9575CD;"></span>
                                </v-list-item-avatar>
                            </v-list-item>

                            <v-card-actions>
                                <v-btn 
                                    class="mx-auto"
                                    outlined
                                    rounded
                                    key="progressViews"
                                    to="/progressViews"
                                    @click="changeUrl()"
                                    color="deep-purple lighten-2"
                                    style="font-weight:500; font-size:20px; padding:15px;"
                                >
                                    ProgressView
                                </v-btn>
                            </v-card-actions>
                        </v-card>
                </v-row>
            </v-container>
        </v-main>
    </v-app>
</template>

<script>

export default {
    name: "App",

    data: () => ({
        useComponent: "",
        drawer: true,
        components: [],
        sideBar: true,
        urlPath: null,
    }),
    
    async created() {
      var path = document.location.href.split("#/")
      this.urlPath = path[1];

    },

    mounted() {
        var me = this;
        me.components = this.$ManagerLists;
    },

    methods: {
        openSideBar(){
            this.sideBar = !this.sideBar
        },
        changeUrl() {
            var path = document.location.href.split("#/")
            this.urlPath = path[1];
        },
        goHome() {
            this.urlPath = null;
        },
    }
};
</script>
<style>
</style>
