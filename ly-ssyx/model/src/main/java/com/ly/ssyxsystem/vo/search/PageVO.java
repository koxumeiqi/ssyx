package com.ly.ssyxsystem.vo.search;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {

    private List<T> content;

    private boolean last;

    private boolean first;

    private boolean empty;

}
