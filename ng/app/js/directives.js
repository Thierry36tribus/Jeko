'use strict';

angular.module('myApp.directives', [])
.directive('appVersion', ['version', function(version) {
    return function(scope, elm, attrs) {
      elm.text(version)
    }
}])
.directive('processing', function() {
  return {
    scope: true,
    link: function(scope, iElement, iAttrs) {
      scope.$sketch = new Processing(iElement[0], scope[iAttrs.processing]);
    }
  }
})
.directive("drawing", function(){
  return {
    restrict: "A",
    link: function(scope, element){
        var ctx = element[0].getContext('2d');
        var img = new Image();
        img.onload = function() {
            ctx.drawImage(img, 0, 0);
        }
        img.src = "img/tree-patterns.svg"     
  }
}})
.directive("t36TimeMachine", function(){
    return {
        restrict: "E",
        templateUrl:'templates/time-machine.html',
        transclude: true,
    }
})
.directive("t36Points", function(){
    return {
        restrict: "E",
        templateUrl:'templates/points.html',
        transclude: true,
    }
})
.directive("amchart", function(){
    return {
        restrict: "E",
        template:'<div></div>',
        replace: true,
        scope: {
            id: '@',
            class: '=',
            data: '=',
            details: '@',
            max: '@',
        },
        link : function(scope, element, attrs){
            scope.$watch('data',function(data){
                if (data && attrs.id) {
                    Chart.createChart(data,attrs.id,attrs.details,attrs.max)
                } 
            })
        }
    }
})
.directive("amchartplus", function(){
    return {
        restrict: "E",
        template:'<div></div>',
        replace: true,
        scope: {
            id: '@',
            class: '=',
            data: '=',
            unit: '@'
        },
        link : function(scope, element, attrs){
            scope.$watch('data',function(data){
                if (data && attrs.id) {
                    window.setTimeout(function(){
                        Chart.createChartPlus(data,attrs.id,attrs.unit)
                    },100)
                } 
            })
        }
    }
})
.directive("amcharts", function(){
    return {
        restrict: "E",
        template:'<div></div>',
        replace: true,
        scope: {
            id: '@',
            class: '=',
            data: '=',
            details: '@'
        },
        link : function(scope, element, attrs){
            scope.$watch('data',function(data){
                if (data && attrs.id) {
                    Chart.createCharts(data,attrs.id)
                } 
            })
        }
    }
})
.directive("gauge", function(){
    return {
        restrict: "E",
        template:'<div></div>',
        replace: true,
        scope: {
            id: '@',
            class: '=',
            data: '=',
        },
        link : function(scope, element, attrs){
            scope.$watch('data',function(data){
                if (data && attrs.id) {
                    window.setTimeout(function(){
                        Chart.createGauge(data,attrs.id)                        
                    },100)
                } 
            })
        }
    }
})

