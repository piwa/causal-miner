var TableProperties = function () {

    function getProcessesTable(ajaxUrl, serverSideConfig) {

        if (serverSideConfig === undefined) {
            serverSideConfig = false;
        }

        if (ajaxUrl === undefined) {
            return {
                autoWidth: false,
                processing: true,
                searching: true,
                serverSide: serverSideConfig,
                pagingType: "simple",
                lengthChange: false,
                info: false,
                select: {
                    style: 'single',
                    items: 'row',
                    info: false
                },
                dom: "<'row'<'col-sm-12 col-md-4'l><'col-sm-12 col-md-8'f>><'row'<'col-sm-12'tr>><'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",
                columns: [
                    // {data: "instanceIdLabel", searchable: false},
                    // {data: "origIdLabel"},
                    // {data: "firmNr"},
                    {data: "origId"},
                    {data: "instanceIdFull", searchable: true, visible: false}
                ]
            };
        }

        return {
            autoWidth: false,
            processing: true,
            searching: true,
            serverSide: serverSideConfig,
            pagingType: "simple",
            info: false,
            lengthChange: false,
            select: {
                style: 'single',
                items: 'row',
                info: false
            },
            dom: "<'row'<'col-sm-12 col-md-4'l><'col-sm-12 col-md-8'f>><'row'<'col-sm-12'tr>><'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",
            ajax: {
                url: ajaxUrl,
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                data: function (d) {
                    return JSON.stringify(d);
                }
            },
            columns: [
                // {data: "instanceIdLabel", searchable: false},
                // {data: "origIdLabel"},
                // {data: "firmNr"},
                {data: "origId"},
                {data: "instanceIdFull", searchable: true, visible: false}
            ]
        };
    }

    function getConnectedInstanceAmounts(ajaxUrl) {

        if (ajaxUrl === undefined) {
            return {
                autoWidth: false,
                processing: true,
                searching: true,
                pagingType: "simple",
                info: false,
                lengthChange: false,
                select: {
                    style: 'single',
                    items: 'row',
                    info: false
                },
                dom: "<'row'<'col-sm-12 col-md-4'l><'col-sm-12 col-md-8'f>><'row'<'col-sm-12'tr>><'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",
                order: [[ 1, 'desc' ]],
                columns: [
                    {data: "origId"},
                    {data: "amount"},
                    {data: "eventTypeLabel", searchable: true, visible: false}
                ]
            };
        }

        return {
            autoWidth: false,
            processing: true,
            searching: true,
            pagingType: "simple",
            info: false,
            lengthChange: false,
            select: {
                style: 'single',
                items: 'row',
                info: false
            },
            dom: "<'row'<'col-sm-12 col-md-4'l><'col-sm-12 col-md-8'f>><'row'<'col-sm-12'tr>><'row'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",
            ajax: {
                url: ajaxUrl,
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                data: function (d) {
                    return JSON.stringify(d);
                }
            },
            order: [[ 1, 'desc' ]],
            columns: [
                {data: "origId"},
                {data: "amount"},
                // {data: "firmNr"},
                // {data: "origId"},
                {data: "eventTypeLabel", searchable: true, visible: false}
            ]
        };
    }


    function getDetailsTableVertical() {
        return {
            paging: false,
            info: false,
            lengthChange: false,
            autoWidth: false,
            processing: false,
            searching: false,
            pagingType: "simple",
            select: {
                style: 'single',
                items: 'row',
                info: false
            },
            columns: [
                {data: "type"},
                {data: "value"}
            ]
        };
    }

    function getProcessInstanceViolationsTableVertical() {
        return {
            paging: false,
            info: false,
            lengthChange: false,
            autoWidth: false,
            processing: false,
            searching: false,
            pagingType: "simple",
            select: {
                style: 'single',
                items: 'row',
                info: false
            },
            columns: [
                {data: "violationType"},
                {data: "violationText"},
                {data: "startNodeLabel"},
                {data: "endNodeLabel"}
            ]
        };
    }

    return {
        getProcessesTable: getProcessesTable,
        getDetailsTableVertical: getDetailsTableVertical,
        getConnectedInstanceAmounts: getConnectedInstanceAmounts,
        getProcessInstanceViolationsTableVertical: getProcessInstanceViolationsTableVertical
    };
}();


var TableUtils = function () {
    function verticalOnProcessElementClick(clickedElement) {
        var detailsTable = $(clickedElement.tableElementId).DataTable();
        detailsTable.clear();

        var properties = [];
        Object.keys(clickedElement.properties).forEach((property) => {
            var row = {};
            row.type = JSON.stringify(property).replace(/"/g, '');
            row.value = JSON.stringify(clickedElement.properties[property]).replace(/"/g, '');
            properties.push(row);
        });

        detailsTable.rows.add(properties).draw();
    }

    return {
        verticalOnProcessElementClick: verticalOnProcessElementClick
    };
}();
