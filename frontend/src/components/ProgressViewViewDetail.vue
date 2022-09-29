<template>

    <v-card outlined>
        <v-card-title>
            ProgressView # {{$route.params.id }}
        </v-card-title>

        <v-card-text>
            <div>
                <String label="Symtom" v-model="item.symtom" :editMode="editMode" @change="change" />
            </div>
            <div>
                <String label="ProductId" v-model="item.productId" :editMode="editMode" @change="change" />
            </div>
            <div>
                <String label="CustomerName" v-model="item.customerName" :editMode="editMode" @change="change" />
            </div>
            <div>
                <String label="PhoneNumber" v-model="item.phoneNumber" :editMode="editMode" @change="change" />
            </div>
            <div>
                <Date label="RequestDate" v-model="item.requestDate" :editMode="editMode" @change="change" />
            </div>
            <div>
                <Number label="RequestId" v-model="item.requestId" :editMode="editMode" @change="change" />
            </div>
            <div>
                <String label="Status" v-model="item.status" :editMode="editMode" @change="change" />
            </div>
            <div>
                <Date label="ServiceDate" v-model="item.serviceDate" :editMode="editMode" @change="change" />
            </div>
        </v-card-text>
    </v-card>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'ProgressViewViewDetail',
    props: {
      value: Object,
    },
    data: () => ({
        item : [],
    }),
    async created() {
      var params = this.$route.params;
      var temp = await axios.get(axios.fixUrl('/progressViews/' + params.id))

      this.item = temp.data;

    },
    methods: {
    }
  }
</script>

