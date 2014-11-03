'use strict';
/** duScroll from https://github.com/durated/angular-scroll */
/* ngStorage from https://github.com/gsklee/ngStorage */

angular.module('myApp', [
    'ngAnimate',
    'ngRoute',
    'duScroll',
    'ngCookies',
    'ngTouch',
    'ngStorage',
    'angularFileUpload', 
    'angularSpinner', 
    'ui.bootstrap',
    'angular-intro',
    'myApp.filters',
    'myApp.services',
    'myApp.directives',
    'myApp.controllers',
    'myApp.All-controller',
    'myApp.Me-controller',
    'myApp.admin.controllers'
]).
config(['$routeProvider', function($routeProvider) {
    //$routeProvider.when('/home', {templateUrl: 'partials/home.html', controller: 'HomeCtrl'});
    $routeProvider.when('/', {templateUrl: 'partials/all.html', controller: 'AllCtrl'});
    $routeProvider.when('/all/:dwellingId', {templateUrl: 'partials/all.html', controller: 'AllCtrl'});
    $routeProvider.when('/me/:dwellingId/:magicNumber', {templateUrl: 'partials/me.html', controller: 'MeCtrl'});
    $routeProvider.when('/custom', {templateUrl: 'partials/custom.html', controller: 'CustomCtrl'});
    $routeProvider.when('/shop', {templateUrl: 'partials/shop.html', controller: 'ShopCtrl'});
    $routeProvider.when('/games', {templateUrl: 'partials/games.html', controller: 'GamesCtrl'});
    $routeProvider.when('/quizz', {templateUrl: 'partials/quizz.html', controller: 'QuizzCtrl'});
    $routeProvider.when('/account', {templateUrl: 'partials/account.html', controller: 'AccountCtrl'});
    $routeProvider.when('/charts', {templateUrl: 'partials/charts.html', controller: 'ChartsCtrl'});
    $routeProvider.when('/admin', {templateUrl: 'partials/admin.html', controller: 'AdminCtrl'});
    $routeProvider.when('/infos', {templateUrl: 'partials/infos.html', controller: 'InfosCtrl'});
    $routeProvider.when('/config', {templateUrl: 'partials/config.html', controller: 'ConfigCtrl'});
    $routeProvider.when('/user/:userId', {templateUrl: 'partials/user.html', controller: 'UserCtrl'});
    $routeProvider.when('/newuser', {templateUrl: 'partials/user.html', controller: 'UserCtrl'});
    $routeProvider.when('/dwelling/:dwellingId', {templateUrl: 'partials/dwelling.html', controller: 'DwellingCtrl'});
    $routeProvider.when('/newdwelling', {templateUrl: 'partials/dwelling.html', controller: 'DwellingCtrl'});
    $routeProvider.when('/byebye', {templateUrl: 'partials/byebye.html', controller: 'ByeByeCtrl'});
    $routeProvider.otherwise({redirectTo: '/'});
}])
/*.config(['$animateProvider', function($animateProvider){
  // restrict animation to these elements 
  $animateProvider.classNameFilter(/jeko-animate/)
}])
*/
window.matchMediaPhone = function() {
    return matchMedia('(max-width: 767px)').matches;
}

