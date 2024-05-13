package com.example.ryanazrian.livechat.payload.response;

import java.util.ArrayList;
import java.util.List;

public class GenericResponseList {
    private List<?> list = new ArrayList<>();

    public GenericResponseList(List<?> list) {
        this.list = list;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
