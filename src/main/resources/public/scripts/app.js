//-------------------------------------------------------------------------------------------------------------//
// Code based on a tutorial by Shekhar Gulati of SparkJava at
// https://blog.openshift.com/developing-single-page-web-applications-using-java-8-spark-mongodb-and-angularjs/
//-------------------------------------------------------------------------------------------------------------//

var app = angular.module('todoapp', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngRoute'
]);

app.config(function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'views/list.html',
        controller: 'ListCtrl'
    }).when('/create', {
        templateUrl: 'views/create.html',
        controller: 'CreateCtrl'
    }).when('/edit/:id', {
        templateUrl: 'views/edit.html',
        controller: 'EditCtrl'
    }).otherwise({
        redirectTo: '/'
    })
});

app.controller('ListCtrl', function ($scope, $http, $location) {

    $scope.getTodos = function() {
        $http.get('/api/v1/todos').success(function (data) {
            $scope.todos = data;
        }).error(function (data, status) {
            console.log('Error ' + data)
        })
    }

    $scope.todoStatusChanged = function (todo) {
        console.log(todo);
        $http.put('/api/v1/todos/' + todo.id, todo).success(function (data) {
            console.log('status changed');
        }).error(function (data, status) {
            console.log('Error ' + data)
        })
    }

    $scope.deleteTodo = function (todo) {
        console.log(todo);
        $http.delete('/api/v1/todos/' + todo.id).success(function (data) {
            console.log('Todo deleted');
            $scope.getTodos();
        }).error(function (data, status) {
            console.log('Error ' + data)
        })
    }

    $scope.getTodos();
});

app.controller('CreateCtrl', function ($scope, $http, $location) {
    $scope.todo = {
        done: false
    };

    $scope.createTodo = function () {
        console.log($scope.todo);
        $scope.todo.createdOn = moment.utc().format('YYYY-MM-DD[T]HH:mm:ss[Z]');
        $http.post('/api/v1/todos', $scope.todo).success(function (data) {
            $location.path('/');
        }).error(function (data, status) {
            console.log('Error ' + data)
        })
    }
});

app.controller('EditCtrl', function ($scope, $http, $location, $routeParams) {

    console.log($routeParams);
    $http.get('/api/v1/todos/' + $routeParams['id']).success(function (data) {
        $scope.todo = data;
        console.log("Got data");
    }).error(function (data, status) {
        console.log('Error ' + data);
        $scope.todo = null;
        /* Might be useful to show some error message? */
    })


    $scope.updateTodo = function (todo) {
        console.log(todo);
        $http.put('/api/v1/todos/' + todo.id, todo).success(function (data) {
            $location.path('/');
        }).error(function (data, status) {
            console.log('Error ' + data)
        })
    }
});
