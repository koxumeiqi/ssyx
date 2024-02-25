package com.ly.ssyxsystem.model.search;

import lombok.Data;
import org.apache.lucene.spatial3d.geom.GeoPoint;
import org.springframework.data.elasticsearch.annotations.Document;


@Data
@Document(indexName = "leaderes" ,shards = 3,replicas = 1)
public class LeaderEs {

    private Long id;

//    @Field(type = FieldType.Keyword, index = false)
    private String takeName;

    //https://blog.csdn.net/zaishijizhidian/article/details/81015988
    private GeoPoint location; //x:经度 y:纬度

    private String storePath;

    private String detailAddress;

    private Double distance;
}
