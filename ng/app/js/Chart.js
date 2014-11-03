'use strict'

window.Chart = {}

Chart.createChart = function(data,elementId,showDetails,max) {
    var chart = new AmCharts.AmSerialChart()
    chart.handDrawn = true
    chart.dataProvider = data
    chart.creditsPosition ="bottom-right"
    chart.categoryField = "week";

    
    //if (!showDetails) {
        chart.autoMargins = false;
        chart.marginLeft = 0;
        chart.marginRight = 0;
        chart.marginTop = 0;
        chart.marginBottom = 0;
    //}

    var graph = new AmCharts.AmGraph();
    graph.valueField = "value"
    graph.type = "smoothedLine"
    graph.fillAlphas = 1
    graph.fillColors = "yellow"
    graph.lineColor = "yellow"
    graph.lineThickness= 1
    chart.addGraph(graph)
    
    var valueAxis = new AmCharts.ValueAxis();
    valueAxis.gridAlpha = 0
    valueAxis.axisAlpha = 0
    if (max) {
        valueAxis.minimum = 0
        valueAxis.maximum = max
    }
    chart.addValueAxis(valueAxis);

    var categoryAxis = chart.categoryAxis;
    categoryAxis.gridAlpha = 0;
    categoryAxis.axisAlpha = 0;

    chart.write(elementId);
}

var getValueWithUnit = function(value,unit) {
    if (unit == 'm3') {
        var s = value / 1000 + ' m3' 
        s = s.replace('.',',')
        return s
    }
    return value + ' ' + unit

}

Chart.createChartPlus = function(data,elementId,unit) {
    var panel = {
        stockGraphs: [{
            type: 'smoothedLine',
            id: "vGraph",
            valueField: "value",
            fillAlphas: 1,
            useDataSetColors:false,
            lineColor: "yellow",
            balloonFunction: function(graphDataItem) {
                var value = graphDataItem.values.value
                return getValueWithUnit(value,unit)
            },
        }],
        valueAxes: [{
            id:"v1",
            labelFunction: function(value) {
                return getValueWithUnit(value,unit)
            }
        }]
    }
        
    var chart = AmCharts.makeChart(elementId, {
        type: "stock",
        pathToImages: "http://cdn.amcharts.com/lib/3/images/",
        theme: "none",
        chartCursorSettings: {
            bulletsEnabled: true,
            zoomable: false,
            valueBalloonsEnabled: true,
            categoryBalloonEnabled : true,
            categoryBalloonDateFormats: [
                {period:"YYYY", format:"YYYY"}, 
                {period:"MM", format:"MM/YYYY"}, 
                {period:"WW", format:"DD/MM"}, 
                {period:"DD", format:"DD/MM"}, 
                {period:"hh", format:"DD/MM JJ:NN"}, 
                {period:"mm", format:"JJ:NN"}, 
                {period:"ss", format:"JJ:NN:SS"}, 
                {period:"fff", format:"JJ:NN:SS"}
            ]

        },
        categoryAxesSettings: {
            minPeriod: 'DD',
            boldPeriodBeginning: false,
            /* ne pas grouper */
            maxSeries :0,
            equalSpacing : true,
            dateFormats : [
                {period:'fff',format:'JJ:NN:SS'},
                {period:'ss',format:'JJ:NN:SS'},
                {period:'mm',format:'JJ:NN'},
                {period:'hh',format:'JJ:NN'},
                {period:'DD',format:'DD/MM'},
                {period:'WW',format:'DD/MM'},
                {period:'MM',format:'DD/MM'},
                {period:'YYYY',format:'DD/MM/YYYY'}
            ]
        },

        dataSets: [{
            fieldMappings: [{
                fromField: "value",
                toField: "value"
            }],

            dataProvider: data,
            categoryField: "date"
        }],

        chartScrollbarSettings: {
            enabled: false,
            graphType: "line"
        }, 
        panels : [panel]
    })
}

Chart.createCharts = function(data,elementId) {
    
    var createGraph = function(field,color,lineThickness) {
    var graph = new AmCharts.AmGraph();
        graph.valueField = field
        graph.type = "line"
        graph.fillAlphas = 0
        graph.showBalloon = true
        graph.lineColor = color
        graph.lineThickness= lineThickness
        return graph
    }
    
    var chart = new AmCharts.AmSerialChart()
    chart.dataProvider = data
    chart.creditsPosition ="bottom-right"
    chart.categoryField = "week"
    
    //chart.addGraph(createGraph("global",'#333333'))
    chart.addGraph(createGraph("fruits",'white',2))
    chart.addGraph(createGraph("heatingCons",'orange',1))
    chart.addGraph(createGraph("electricityCons",'yellow',1))
    chart.addGraph(createGraph("waterCons",'blue',1))
    chart.addGraph(createGraph("hotWaterCons",'red',1))
    chart.addGraph(createGraph("global",'black',1))
    
    var valueAxis = new AmCharts.ValueAxis();
    valueAxis.gridAlpha = 1
    valueAxis.axisAlpha = 1
    chart.addValueAxis(valueAxis);

    var categoryAxis = chart.categoryAxis;
    categoryAxis.gridAlpha = 0;
    categoryAxis.axisAlpha = 0;

    chart.write(elementId);
}
Chart.createGauge = function(gaugeValue,elementId) {
    AmCharts.makeChart(elementId,
        {
            "type": "gauge",
            "pathToImages": "http://cdn.amcharts.com/lib/3/images/",
            creditsPosition:"bottom-right",
            autoMargins: false,
            marginLeft: 0,
            marginRight:0,
            "marginBottom": 0,
            "marginTop": 0,
            "fontSize": 17,
            "theme": "default",
        	"startDuration": 3,
            "startEffect": "elastic",
            "arrows": [
                {
                    "id": "GaugeArrow-1",
                    "innerRadius": "0%",
                    "nailRadius": (elementId == 'gaugeSmall' ? 4 : 7),
                    "startWidth": 5,
                    "value": gaugeValue
                }
            ],
            "axes": [
                {
                    "axisThickness": 0,
                    "bottomText": "",
                    "endAngle": 90,
                    "endValue": 100,
                    "gridCount": 1,
                    "gridInside": false,
                    "id": "GaugeAxis-1%",
                    "labelFrequency": 0,
                    "labelOffset": 0,
                    "minorTickLength": 0,
                    "radius": "77%",
                    "showFirstLabel": false,
                    "showLastLabel": false,
                    "startAngle": -90,
                    "tickColor": "#FFFFFF",
                    "tickLength": 0,
                    "tickThickness": 0,
                    "valueInterval": 10,
                    "bands": [
                        {
                            "alpha": 1,
                            "color": (elementId == 'gaugeSmall' ? "#B2E375" : "#7DE60A"),
                            "endValue": 90,
                            "id": "GaugeBand-1",
                            "innerRadius": "70%",//"0%"
                            "startValue": 0
                        },
                        {
                            "alpha": 1,
                            "color": "#F90606",
                            "endValue": 100,
                            "id": "GaugeBand-3",
                            "innerRadius": "70%", //"0%"
                            "startValue": 90
                        }
                    ]
                }
            ],
            "allLabels": [],
            "balloon": {},
            "titles": []
        }        
    )  
}

