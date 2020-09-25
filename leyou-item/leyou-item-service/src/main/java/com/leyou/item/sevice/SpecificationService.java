package com.leyou.item.sevice;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.runtime.Log;

import java.util.List;

@Service
public class SpecificationService {


    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> querySpecGroupByCid(Long cid) {

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.specGroupMapper.select(specGroup);
    }


    /**
     * 通过条件查询规格参数
     * @param param
     * @return
     */
    public List<SpecParam> querySpecParamByCondition(SpecParam param) {
       /* SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);*/

        return this.specParamMapper.select(param);
    }

    public void insertSpecParam(SpecParam param) {

        this.specParamMapper.insert(param);
    }

    public void updateSpecParam(SpecParam param) {
        this.specParamMapper.updateByPrimaryKeySelective(param);
    }

    public void deleteSpecParamById(Long id) {
        this.specParamMapper.deleteByPrimaryKey(id);
    }

    public void insertSpecGroup(SpecGroup group) {

        this.specGroupMapper.insert(group);
    }

    public void editSpecGroup(SpecGroup group) {
        this.specGroupMapper.updateByPrimaryKeySelective(group);
    }

    @Transactional
    public void deleteSpecGroupById(Long id) {

        this.specGroupMapper.deleteByPrimaryKey(id);
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(id);
        this.specParamMapper.delete(specParam);
    }


    /**
     * 查询分类下所有组及信息
     * @param cid
     * @return
     */
    public List<SpecGroup> querySpecGroupByCid2(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> specGroups = this.specGroupMapper.select(specGroup);

        specGroups.forEach(specGroup1 ->{
            SpecParam specParam = new SpecParam();
            specParam.setGroupId(specGroup1.getId());

            List<SpecParam> specParams = this.specParamMapper.select(specParam);
            specGroup1.setParams(specParams);
        });

        return specGroups;
    }
}
