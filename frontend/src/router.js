
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import RequestManager from "./components/listers/RequestCards"
import RequestDetail from "./components/listers/RequestDetail"

import ReadServiceListView from "./components/ReadServiceListView"
import ReadServiceListViewDetail from "./components/ReadServiceListViewDetail"
import ReadRequestView from "./components/ReadRequestView"
import ReadRequestViewDetail from "./components/ReadRequestViewDetail"
import ServiceManager from "./components/listers/ServiceCards"
import ServiceDetail from "./components/listers/ServiceDetail"

import StockManager from "./components/listers/StockCards"
import StockDetail from "./components/listers/StockDetail"

import PayManager from "./components/listers/PayCards"
import PayDetail from "./components/listers/PayDetail"


import ProgressViewView from "./components/ProgressViewView"
import ProgressViewViewDetail from "./components/ProgressViewViewDetail"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/requests',
                name: 'RequestManager',
                component: RequestManager
            },
            {
                path: '/requests/:id',
                name: 'RequestDetail',
                component: RequestDetail
            },

            {
                path: '/readServiceLists',
                name: 'ReadServiceListView',
                component: ReadServiceListView
            },
            {
                path: '/readServiceLists/:id',
                name: 'ReadServiceListViewDetail',
                component: ReadServiceListViewDetail
            },
            {
                path: '/readRequests',
                name: 'ReadRequestView',
                component: ReadRequestView
            },
            {
                path: '/readRequests/:id',
                name: 'ReadRequestViewDetail',
                component: ReadRequestViewDetail
            },
            {
                path: '/services',
                name: 'ServiceManager',
                component: ServiceManager
            },
            {
                path: '/services/:id',
                name: 'ServiceDetail',
                component: ServiceDetail
            },

            {
                path: '/stocks',
                name: 'StockManager',
                component: StockManager
            },
            {
                path: '/stocks/:id',
                name: 'StockDetail',
                component: StockDetail
            },

            {
                path: '/pays',
                name: 'PayManager',
                component: PayManager
            },
            {
                path: '/pays/:id',
                name: 'PayDetail',
                component: PayDetail
            },


            {
                path: '/progressViews',
                name: 'ProgressViewView',
                component: ProgressViewView
            },
            {
                path: '/progressViews/:id',
                name: 'ProgressViewViewDetail',
                component: ProgressViewViewDetail
            },


    ]
})
