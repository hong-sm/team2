<template>

    <v-data-table
        :headers="headers"
        :items="progressView"
        :items-per-page="5"
        class="elevation-1"
    ></v-data-table>

</template>

<script>
    const axios = require('axios').default;

    export default {
        name: 'ProgressViewView',
        props: {
            value: Object,
            editMode: Boolean,
            isNew: Boolean
        },
        data: () => ({
            headers: [
                { text: "id", value: "id" },
                { text: "symtom", value: "symtom" },
                { text: "productId", value: "productId" },
                { text: "customerName", value: "customerName" },
                { text: "phoneNumber", value: "phoneNumber" },
                { text: "requestDate", value: "requestDate" },
                { text: "requestId", value: "requestId" },
                { text: "status", value: "status" },
                { text: "serviceDate", value: "serviceDate" },
            ],
            progressView : [],
        }),
          async created() {
            var temp = await axios.get(axios.fixUrl('/progressViews'))

            temp.data._embedded.progressViews.map(obj => obj.id=obj._links.self.href.split("/")[obj._links.self.href.split("/").length - 1])

            this.progressView = temp.data._embedded.progressViews;
        },
        methods: {
        }
    }
</script>

