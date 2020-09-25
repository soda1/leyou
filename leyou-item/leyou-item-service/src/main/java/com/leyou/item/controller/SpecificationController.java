package com.leyou.item.controller;


import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.sevice.SpecificationService;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;


import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询分类下的分组，如果查询不到会报404，不一定是真的无法访问页面
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupByCid(@PathVariable("cid") Long cid) {
        if (StringUtils.isEmpty(cid)) {
            return ResponseEntity.badRequest().build();
        }

        List<SpecGroup> specGroups = specificationService.querySpecGroupByCid(cid);

        if (CollectionUtils.isEmpty(specGroups)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(specGroups);
//       return ResponseEntity.ok("hello,everyone");

    }

    @PostMapping("/group")
    public ResponseEntity<Void> insertSpecGroup(SpecGroup group) {

        this.specificationService.insertSpecGroup(group);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/group")
    public ResponseEntity<Void> editSpecGroup(SpecGroup group) {
        this.specificationService.editSpecGroup(group);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/group/{id}")
    public ResponseEntity<Void> deleteSpecGroupById(@PathVariable("id") Long id) {

        this.specificationService.deleteSpecGroupById(id);
        return ResponseEntity.ok(null);
    }

    /* @GetMapping("/hello/{cid}")
     public String test(@PathVariable("cid") Long cid) {
         return "hello" + cid;
     }*/


    /**
     * 通过条件查询规格参数
     * @param specParam
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParamByCondition(SpecParam specParam) {

      /*  SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        specParam.setSku_id(id);*/
        List<SpecParam> specParams = specificationService.querySpecParamByCondition(specParam);

        if (CollectionUtils.isEmpty(specParams)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(specParams);

    }


    /**
     * 通过cid查询分类下所有规格参数
     * @param param
     * @return
     *//*
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParamByCid(SpecParam param) {
        if (param.getCid() == null || param.getCid().longValue() < 0) {
            return ResponseEntity.badRequest().build();
        }
        List<SpecParam> specParams = this.specificationService.querySpecParamByCondition(param);
        if (CollectionUtils.isEmpty(specParams)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specParams);
    }*/


    /**
     * 插入规格参数
     * @param param
     * @return
     */
    @PostMapping("/param")
    public ResponseEntity<Void> insertSpecParam(SpecParam param) {
        specificationService.insertSpecParam(param);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/param")
    public ResponseEntity<Void> updateSpecParam(SpecParam param) {
        specificationService.updateSpecParam(param);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/param/{id}")
    public ResponseEntity deleteSpecParamById(@PathVariable("id") Long id) {
        specificationService.deleteSpecParamById(id);
        return ResponseEntity.ok(null);
    }


    /**
     * 查询分类下的组和具体信息
     * @param cid
     * @return
     */
    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupByCid2(@PathVariable("cid") Long cid) {
        List<SpecGroup> specGroups = this.specificationService.querySpecGroupByCid2(cid);

        if (CollectionUtils.isEmpty(specGroups)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(specGroups);
    }


}
