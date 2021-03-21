package com.oschain.fastchaindb.system.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.system.dao.DownloadRecordMapper;
import com.oschain.fastchaindb.system.model.Console;
import com.oschain.fastchaindb.system.model.DownloadRecord;

import com.oschain.fastchaindb.system.service.DownloadRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DownloadRecordServiceImpl implements DownloadRecordService {
    @Autowired
    private DownloadRecordMapper downloadRecordMapper;

    @Override
    public List<Console> sumPastDownloadRecord(){ return downloadRecordMapper.sumPastDownloadRecord();};

    @Override
    public Integer getDownloadTotal() {
        return downloadRecordMapper.getDownloadTotal();
    }

    @Override
    public boolean add(DownloadRecord downloadRecord) {
        downloadRecord.setDownloadTime(new Date());
        return downloadRecordMapper.insert(downloadRecord) > 0;
    }

    @Override
    public PageResult<DownloadRecord> list(int pageNum, int pageSize, String startDate, String endDate, String account) {
        Page<DownloadRecord> page = new Page<>(pageNum, pageSize);
        List<DownloadRecord> records = downloadRecordMapper.listFull(page, startDate, endDate, account);
        return new PageResult<>(page.getTotal(), records);
    }

    @Override
    public boolean insert(DownloadRecord entity) {
        return false;
    }

    @Override
    public boolean insertAllColumn(DownloadRecord entity) {
        return false;
    }

    @Override
    public boolean insertBatch(List<DownloadRecord> entityList) {
        return false;
    }

    @Override
    public boolean insertBatch(List<DownloadRecord> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean insertOrUpdateBatch(List<DownloadRecord> entityList) {
        return false;
    }

    @Override
    public boolean insertOrUpdateBatch(List<DownloadRecord> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean insertOrUpdateAllColumnBatch(List<DownloadRecord> entityList) {
        return false;
    }

    @Override
    public boolean insertOrUpdateAllColumnBatch(List<DownloadRecord> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean deleteById(Serializable id) {
        return false;
    }

    @Override
    public boolean deleteByMap(Map<String, Object> columnMap) {
        return false;
    }

    @Override
    public boolean delete(Wrapper<DownloadRecord> wrapper) {
        return false;
    }

    @Override
    public boolean deleteBatchIds(Collection<? extends Serializable> idList) {
        return false;
    }

    @Override
    public boolean updateById(DownloadRecord entity) {
        return false;
    }

    @Override
    public boolean updateAllColumnById(DownloadRecord entity) {
        return false;
    }

    @Override
    public boolean update(DownloadRecord entity, Wrapper<DownloadRecord> wrapper) {
        return false;
    }

    @Override
    public boolean updateForSet(String setStr, Wrapper<DownloadRecord> wrapper) {
        return false;
    }

    @Override
    public boolean updateBatchById(List<DownloadRecord> entityList) {
        return false;
    }

    @Override
    public boolean updateBatchById(List<DownloadRecord> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateAllColumnBatchById(List<DownloadRecord> entityList) {
        return false;
    }

    @Override
    public boolean updateAllColumnBatchById(List<DownloadRecord> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean insertOrUpdate(DownloadRecord entity) {
        return false;
    }

    @Override
    public boolean insertOrUpdateAllColumn(DownloadRecord entity) {
        return false;
    }

    @Override
    public DownloadRecord selectById(Serializable id) {
        return null;
    }

    @Override
    public List<DownloadRecord> selectBatchIds(Collection<? extends Serializable> idList) {
        return null;
    }

    @Override
    public List<DownloadRecord> selectByMap(Map<String, Object> columnMap) {
        return null;
    }

    @Override
    public DownloadRecord selectOne(Wrapper<DownloadRecord> wrapper) {
        return null;
    }

    @Override
    public Map<String, Object> selectMap(Wrapper<DownloadRecord> wrapper) {
        return null;
    }

    @Override
    public Object selectObj(Wrapper<DownloadRecord> wrapper) {
        return null;
    }

    @Override
    public int selectCount(Wrapper<DownloadRecord> wrapper) {
        return 0;
    }

    @Override
    public List<DownloadRecord> selectList(Wrapper<DownloadRecord> wrapper) {
        return null;
    }

    @Override
    public Page<DownloadRecord> selectPage(Page<DownloadRecord> page) {
        return null;
    }

    @Override
    public List<Map<String, Object>> selectMaps(Wrapper<DownloadRecord> wrapper) {
        return null;
    }

    @Override
    public List<Object> selectObjs(Wrapper<DownloadRecord> wrapper) {
        return null;
    }

    @Override
    public Page<Map<String, Object>> selectMapsPage(Page page, Wrapper<DownloadRecord> wrapper) {
        return null;
    }

    @Override
    public Page<DownloadRecord> selectPage(Page<DownloadRecord> page, Wrapper<DownloadRecord> wrapper) {
        return null;
    }
}
