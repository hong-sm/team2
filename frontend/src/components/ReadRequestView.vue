<template>

    <v-data-table
        :headers="headers"
        :items="readRequest"
        :items-per-page="5"
        class="elevation-1"
    ></v-data-table>

</template>

<script>
    const axios = require('axios').default;

    export default {
        name: 'ReadRequestView',
        props: {
            value: Object,
            editMode: Boolean,
            isNew: Boolean
        },
        data: () => ({
            headers: [
                { text: "id", value: "id" },
            ],
            readRequest : [],
        }),
          async created() {
            var temp = await axios.get(axios.fixUrl('/readRequests'))

            temp.data._embedded.readRequests.map(obj => obj.id=obj._links.self.href.split("/")[obj._links.self.href.split("/").length - 1])

            this.readRequest = temp.data._embedded.readRequests;
        },
        methods: {
        }
    }
</script>

