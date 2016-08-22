"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var core_1 = require('@angular/core');
var http_1 = require('@angular/http');
require('rxjs/Rx');
var AppComponent = (function () {
    function AppComponent(http) {
        this.http = http;
        this.todos = [];
    }
    AppComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.http.get("http://localhost:8080/api/todo")
            .map(function (result) { return result.json(); })
            .subscribe(function (result) {
            _this.todos = result;
        }, function (error) {
            console.error(error);
        });
    };
    AppComponent.prototype.save = function () {
        var _this = this;
        if (this.title && this.description) {
            var requestHeaders = new http_1.Headers();
            requestHeaders.append("Content-Type", "application/json");
            this.http.request(new http_1.Request({
                method: http_1.RequestMethod.Post,
                url: "http://localhost:8080/api/todo",
                body: JSON.stringify({ title: this.title, description: this.description }),
                headers: requestHeaders
            }))
                .map(function (result) { return result.json(); })
                .subscribe(function (result) {
                _this.todos.push({
                    "id": result.id,
                    "rev": result.rev,
                    "title": _this.title,
                    "description": _this.description
                });
            }, function (error) {
                console.error(error);
            });
        }
    };
    AppComponent.prototype.clear = function () {
        var _this = this;
        var documents = [];
        for (var i = 0; i < this.todos.length; i++) {
            documents.push({ "id": this.todos[i].id, "rev": this.todos[i].rev });
        }
        var requestHeaders = new http_1.Headers();
        requestHeaders.append("Content-Type", "application/json");
        this.http.request(new http_1.Request({
            method: http_1.RequestMethod.Delete,
            url: "http://localhost:8080/api/todo",
            body: JSON.stringify(documents),
            headers: requestHeaders
        }))
            .map(function (result) { return result.json(); })
            .subscribe(function (result) {
            _this.todos = [];
        }, function (error) {
            console.error(error);
        });
    };
    AppComponent = __decorate([
        core_1.Component({
            selector: 'app-root',
            templateUrl: './app/app.component.html'
        }), 
        __metadata('design:paramtypes', [http_1.Http])
    ], AppComponent);
    return AppComponent;
}());
exports.AppComponent = AppComponent;
//# sourceMappingURL=app.component.js.map