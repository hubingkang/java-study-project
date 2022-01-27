package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/*Filter过滤注解使用的实体类*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SendInfo {

    private String name;
    private String info;

}
