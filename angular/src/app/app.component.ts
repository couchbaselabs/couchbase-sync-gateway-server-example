import { Component, OnInit } from '@angular/core';
import { Http, Request, RequestMethod, Headers, HTTP_PROVIDERS } from '@angular/http';
import 'rxjs/Rx';

@Component({
    selector: 'app-root',
    templateUrl: './app/app.component.html'
})
export class AppComponent implements OnInit {

    public todos: Array<any>;
    public title: string;
    public description: string;

    public constructor(private http: Http) {
        this.todos = [];
    }

    public ngOnInit() {
        this.http.get("http://localhost:8080/api/todo")
            .map(result => result.json())
            .subscribe(result => {
                this.todos = result;
            }, error => {
                console.error(error);
            });
    }

    public save() {
        if(this.title && this.description) {
            let requestHeaders = new Headers();
            requestHeaders.append("Content-Type", "application/json");
            this.http.request(new Request({
                method: RequestMethod.Post,
                url: "http://localhost:8080/api/todo",
                body: JSON.stringify({title: this.title, description: this.description}),
                headers: requestHeaders
            }))
            .map(result => result.json())
            .subscribe(result => {
                this.todos.push({
                    "id": result.id,
                    "rev": result.rev,
                    "title": this.title,
                    "description": this.description
                });
            }, error => {
                console.error(error);
            });
        }
    }

    public clear() {
        let documents: Array<any> = [];
        for(let i = 0; i < this.todos.length; i++) {
            documents.push({"id": this.todos[i].id, "rev": this.todos[i].rev});
        }
        let requestHeaders = new Headers();
        requestHeaders.append("Content-Type", "application/json");
        this.http.request(new Request({
            method: RequestMethod.Delete,
            url: "http://localhost:8080/api/todo",
            body: JSON.stringify(documents),
            headers: requestHeaders
        }))
        .map(result => result.json())
        .subscribe(result => {
            this.todos = [];
        }, error => {
            console.error(error);
        });
    }

}
