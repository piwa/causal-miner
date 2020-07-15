var ProcessView = function (options) {
    var defaultUrl = "";
    var processViewId = "";
    var nullNodeToggleId = "";
    var tableElementId = "";
    var withEdgehandles = false;
    var withContextMenu = false;
    var contextConfiguration = "";
    var processDrawingDoneEvent = () => {
    };
    var onProcessElementClick = () => {
    };

    if ($.isPlainObject(options)) {
        if (options.url !== undefined) {
            defaultUrl = options.url;
        }
        if (options.processViewId !== undefined) {
            processViewId = options.processViewId;
        }
        if (options.nullNodeToggleId !== undefined) {
            nullNodeToggleId = options.nullNodeToggleId;
        }
        if (options.tableElementId !== undefined) {
            tableElementId = options.tableElementId;
        }
        if (options.processDrawingDoneEvent !== undefined) {
            processDrawingDoneEvent = options.processDrawingDoneEvent;
        }
        if (options.onProcessElementClick !== undefined) {
            onProcessElementClick = options.onProcessElementClick;
        }
        if(options.withEdgehandles !== undefined) {
            withEdgehandles = options.withEdgehandles;
        }
        if(options.withContextMenu !== undefined) {
            withContextMenu = options.withContextMenu;
        }
        if(options.contextConfiguration !== undefined) {
            contextConfiguration = options.contextConfiguration;
        }
    }

    var cy = cytoscape({
        container: $(processViewId),

        boxSelectionEnabled: false,
        autounselectify: true,

        style: fetch('/js/cy-style.json').then((res) => res.json()),

    });

    if(withEdgehandles === true) {
        cy.edgehandles({
            snap: true
        });
    }
    if(withContextMenu === true) {
        cy.cxtmenu(contextConfiguration);
    }

    var cyLayoutOptions = {
        name: 'dagre',
        rankDir: 'LR'
    };

    cy.on('tap', 'node', function (evt) {
        onProcessElementClick(evt.target.data());
    });
    cy.on('tap', 'edge', function (evt) {
        onProcessElementClick(evt.target.data());
    });

    if (typeof nullNodeToggleId !== "undefined") {
        $(nullNodeToggleId).off("click").on('click', () => {
            cy.startBatch();
            cy.nodes('[nodeType^="NULL"]').forEach((ele) => {
                if (ele.hidden()) {
                    ele.show();
                } else {
                    ele.hide();
                }
            });
            cy.endBatch();
            cy.layout(cyLayoutOptions).run();
        })
    }

    function draw(fetchUrl) {
        var url = defaultUrl;
        if (fetchUrl !== undefined) {
            url = fetchUrl;
        }

        return fetch(url)
            .then((response) => {
                return response.json();
            })
            .then((graphData) => {

                var nodes = drawProcessModel(graphData);

                if (typeof processDrawingDoneEvent !== "undefined") {
                    processDrawingDoneEvent(nodes);
                }
            });
    }

    function drawProcessModel(graphData) {

        if (typeof tableElementId !== "undefined") {
            graphData.nodes.forEach((node) => {
                node.data.tableElementId = tableElementId;
            });

            graphData.edges.forEach((edge) => {
                edge.data.tableElementId = tableElementId;
            });
        }

        graphData.edges.forEach((edge) => {
            if (edge.data.startCardinality !== "" && edge.data.endCardinality !== "" && edge.data.quantity !== "") {
                edge.data.label = edge.data.startCardinality + " : " + edge.data.endCardinality + "\n(" + edge.data.quantity + ")";
            }
            else if (edge.data.startCardinality !== "" && edge.data.endCardinality !== "") {
                edge.data.label = edge.data.startCardinality + " : " + edge.data.endCardinality;
            }
        });

        cy.remove(cy.$());
        cy.add(graphData);
        cy.layout(cyLayoutOptions).run();

        return graphData.nodes;
    }

    function redraw() {
        cy.layout(cyLayoutOptions).run();
    }

    return {
        draw: draw,
        cy: cy,
        redraw: redraw
    };

};