package com.leyou.item.sevice;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.ArrayList;
import java.util.List;


@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryByPid(Long pid) {

        Category record = new Category();
        record.setParentId(pid);
        return this.categoryMapper.select(record);
    }


    public List<Category> queryCategoriesByBrandId(Long bid) {

        return this.categoryMapper.queryCategoriseByBrandId(bid);

    }


    @Transactional
    public void insertCategory(Category category) {
        if (category.getParentId() > 0) {

//            Category category1 = this.categoryMapper.selectByPrimaryKey(category.getParentId());
//            category1.setIsParent(true);

            Category category1 = new Category();
            category1.setId(category.getParentId());
            category1.setIsParent(true);

            this.categoryMapper.updateCategoryById(category1);
        }



        this.categoryMapper.insert(category);
    }

    @Transactional
    public void editCategoryNameById(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        this.categoryMapper.updateCategoryById(category);
    }


    @Transactional
    public void deleteCategory(Category category) {
        //1、删除该子节点下的所有分类
        delete(category.getId());

        //2、要判断删除节点的父类节点是不是只有这一个节点而已，如果是要把isParent置为false

        Category child = this.categoryMapper.selectByPrimaryKey(category.getId());

        //2、1查出节点父类有多少个子类,对是否是顶级父类进行判断
        if (child.getParentId() > 0) {
            Example example = new Example(Category.class);
            Criteria criteria = example.createCriteria();
            criteria.andEqualTo("parentId", child.getParentId());
            List<Category> categories = this.categoryMapper.selectByExample(example);
            //如果查出来父类的子节点只有一个，那么将isParent置为false
            if (categories.size() < 2) {
                Category parent = new Category();
                parent.setId(child.getParentId());
                parent.setIsParent(false);
                this.categoryMapper.updateByPrimaryKeySelective(parent);
            }
        }
        this.categoryMapper.delete(category);

    }

    /**
     * 递归删除子分类及其下面的分类
     * @param pid
     */
    private void delete(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List<Category> select = this.categoryMapper.select(category);
        if (select != null && select.size() > 0) {

            for (Category category1 : select) {
                if (category1.getIsParent()) {
                    delete(category1.getId());
                    this.categoryMapper.delete(category1);
                } else {
                    this.categoryMapper.delete(category1);
                }
            }
        }
    }


    /**
     * 查出分类路径并以字符数组返回
     * @param cid1
     * @param cid2
     * @param cid3
     * @return
     */
    public List<String> queryNamesByIds(Long cid1, Long cid2, Long cid3) {

        List<String> names = new ArrayList<>();
        String cname1 = this.categoryMapper.selectByPrimaryKey(cid1).getName();
        String cname2 = this.categoryMapper.selectByPrimaryKey(cid2).getName();
        String cname3 = this.categoryMapper.selectByPrimaryKey(cid3).getName();

        names.add(cname1);
        names.add(cname2);
        names.add(cname3);
        return names;
    }


    /**
     * 查出分类路径并以字符数组返回
     *
     * @return
     */
    public List<String> queryNamesByIds(List<Long> ids) {

        List<String> names = new ArrayList<>();
        for (Long id : ids) {
            Category category = this.categoryMapper.selectByPrimaryKey(id);

            names.add(category.getName());
        }
        return names;
    }

    public List<Category> queryAllByCid3(Long id) {

        List<Category> categories = new ArrayList<>();
        Category category3 = this.categoryMapper.selectByPrimaryKey(id);
        Category category2 = this.categoryMapper.selectByPrimaryKey(category3.getParentId());
        Category category1 = this.categoryMapper.selectByPrimaryKey(category2.getParentId());
        categories.add(category1);
        categories.add(category2);
        categories.add(category3);
        return categories;


    }
}
