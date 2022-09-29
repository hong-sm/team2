<template>

    <v-data-table
        :headers="headers"
        :items="readServiceList"
        :items-per-page="5"
        class="elevation-1"
    ></v-data-table>

</template>

<script>
    const axios = require('axios').default;

    export default {
        name: 'ReadServiceListView',
        props: {
            value: Object,
            editMode: Boolean,
            isNew: Boolean
        },
        data: () => ({
            headers: [
                { text: "id", value: "id" },
            ],
            readServiceList : [],
        }),
          async created() {
            var temp = await axios.get(axios.fixUrl('/readServiceLists'))

            temp.data._embedded.readServiceLists.map(obj => obj.id=obj._links.self.href.split("/")[obj._links.self.href.split("/").length - 1])

            this.readServiceList = temp.data._embedded.readServiceLists;
        },
        methods: {
        }
    }
</script>

