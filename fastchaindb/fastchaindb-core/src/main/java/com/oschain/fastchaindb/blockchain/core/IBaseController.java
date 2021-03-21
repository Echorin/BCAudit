package com.oschain.fastchaindb.blockchain.core;

import com.oschain.fastchaindb.blockchain.dto.FCDBAsset;
import com.oschain.fastchaindb.blockchain.dto.FCDBBlock;
import com.oschain.fastchaindb.blockchain.dto.FCDBTransationQuery;
import com.oschain.fastchaindb.common.JsonResult;
import com.oschain.fastchaindb.common.ResultDTO;
import org.springframework.web.bind.annotation.RequestBody;

public interface IBaseController {
    public JsonResult createPairKey(String args);
    public ResultDTO<String> sendTransaction(@RequestBody FCDBAsset asset) throws Exception;
    public ResultDTO<FCDBBlock> getChainByTransactionId(@RequestBody FCDBTransationQuery transationQuery) throws Exception;
    public ResultDTO<FCDBBlock> getChainByBlockHeight(@RequestBody FCDBTransationQuery transationQuery) throws Exception;
    public ResultDTO<FCDBBlock> getChainByBlockHash(@RequestBody FCDBTransationQuery transationQuery) throws Exception;
}
