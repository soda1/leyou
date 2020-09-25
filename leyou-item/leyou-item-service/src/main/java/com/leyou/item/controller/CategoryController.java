package com.leyou.item.controller;


import com.leyou.item.pojo.Category;
import com.leyou.item.sevice.CategoryService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam("pid") Long pid) {
        if (pid == null || pid.longValue() < 0) {
//            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = categoryService.queryCategoryByPid(pid);
        if (CollectionUtils.isEmpty(categories)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);

    }


    @GetMapping("/bid/{bid}")
    public ResponseEntity<List<Category>> queryCategoriesByBrandId( @PathVariable("bid") Long bid) {

        if (bid == null || bid.longValue() < 0) {
            return ResponseEntity.badRequest().build();
        }

        List<Category> categories = categoryService.queryCategoriesByBrandId(bid);
        if (CollectionUtils.isEmpty(categories)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categories);
    }


    @PostMapping
    public ResponseEntity<Void> insertCategory(Category category, HttpServletRequest request) {
        if (category == null || category.getName() == null) {
            return ResponseEntity.badRequest().build();
        }

       /* String isParent = request.getParameter("isParent");
        if (StringUtils.isEmpty(false)) {
            return ResponseEntity.badRequest().build();
        } else{
            if (isParent .equals("false") ) {
                category.setIsParent(false);
            } else {
                category.setIsParent(true);
            }
        }*/

        categoryService.insertCategory(category);
        return ResponseEntity.ok(null);
    }

    @PutMapping
    public ResponseEntity<Void> editCategory(@RequestParam("id") Long id, @RequestParam("name") String name) {
        if ((id == null && id.longValue() < 0) || StringUtils.isEmpty(name)) {
            return ResponseEntity.badRequest().build();
        }

        categoryService.editCategoryNameById(id, name);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCategory(Category category) {
        //必须保证id>0，否则会删除整个表格
        if (category == null || category.getId() == null || category.getId().longValue() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        this.categoryService.deleteCategory(category);
        return ResponseEntity.ok(null);
    }


    @GetMapping("names")
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids") List<Long> ids) {


        List<String> names = this.categoryService.queryNamesByIds(ids);
        if (CollectionUtils.isEmpty(names)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(names);
    }


    /**
     * 根据3级id查询商品
     * @param id
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long id) {
        if (id == null || id.longValue() < 0) {
            return ResponseEntity.badRequest().build();
        }

        List<Category> list = this.categoryService.queryAllByCid3(id);
        if (list == null || list.size() < 1) {
            return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(list);
    }




}