package io.github.tastac.bfj.api;

public class RequestBuilder {

    private StringBuilder requestTail = new StringBuilder();

    //TODO add documentaion to these methods

    public RequestBuilder(String table){
        requestTail.append(table);
    }

    public RequestBuilder addSearchQuery(String dataTag, String data){
        requestTail.append("&").append(dataTag).append("=").append(data);
        return this;
    }

    public String getString(){ return requestTail.toString(); }

}
