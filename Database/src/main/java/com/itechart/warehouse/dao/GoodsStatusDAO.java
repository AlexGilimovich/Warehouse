package com.itechart.warehouse.dao;


import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.entity.GoodsStatus;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementation of goodsIdList status DAO.
 */
@Repository
public class GoodsStatusDAO extends DAO<GoodsStatus> {
    public GoodsStatusDAO() {
        super(GoodsStatus.class);
    }

    public GoodsStatus findCurrentByGoodsId(Long goodsId) throws GenericDAOException {
        logger.info("Find find current status of goods with id: {}", goodsId);
        String queryHql = "SELECT status FROM GoodsStatus status" +
                " INNER JOIN Goods goods ON status.goods=goods" +
                " WHERE goods.id = :goodsId AND goods.deleted IS NULL ORDER BY status.date DESC";
        Query<GoodsStatus> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("goodsId", goodsId);
        query.setMaxResults(1);
        return query.getSingleResult();
    }


    public List<GoodsStatus> findByGoodsId(Long goodsId) throws GenericDAOException {
        logger.info("Find list of statuses for goods with id: {}", goodsId);
        String queryHql = "SELECT DISTINCT status FROM GoodsStatus status" +
                " INNER JOIN Goods goods ON goods = status.goods" +
                " WHERE goods.id = :goodsId AND goods.deleted IS NULL";
        Query<GoodsStatus> query = hibernateTemplate.getSessionFactory().getCurrentSession().createQuery(queryHql);
        query.setParameter("goodsId", goodsId);
        return query.list();
    }
}
