package com.xuecheng.content.service.impl;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程基本信息管理业务接口
 * @date 2022/9/6 21:42
 */

public class CourseCategoryServiceImpl implements CourseCategoryService {


    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    /**
     * 课程分类树形结构查询
     * @return
     */
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {

        //调用mapper递归查出分类信息
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        //List转成map，key为结点id，value为CourseCategoryTreeDto对象，为了方便从map中获取结点 过滤根节点
        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).collect(
                Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2)
        );


        //定义一个list作为最终返回的list
        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList<>();

        //找到每个节点的子节点，最终 封装成List<CourseCategoryTreeDto>
        //依次遍历每个元素,排除根节点
        //从头遍历List<CourseCategoryTreeDto>，一边遍历一边找子节点放在父节点的childrenTreeNodes
        courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).forEach(item ->{
            if (item.getParentid().equals(id)){
                //将子节点添加进categoryTreeDtos
                categoryTreeDtos.add(item);
            }
            //找到当前节点的父节点
            CourseCategoryTreeDto courseCategoryTreeDto = mapTemp.get(item.getParentid());
            if (courseCategoryTreeDto != null ){
                //存在父节点
                if (courseCategoryTreeDto.getChildrenTreeNodes() == null){
                    //若父节点中的childrenTreeNodes属性为空 新建空间放子节点
                    courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                //下边开始往ChildrenTreeNodes属性中放子节点
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });


        return categoryTreeDtos;
    }



}

