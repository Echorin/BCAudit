package com.oschain.fastchaindb.system.dao;

import com.oschain.fastchaindb.system.model.CertCheck;

public interface HomeMapper {
    Integer sumFileNum();
    Integer sumUserNum();
    CertCheck certInfo(String filehash);
    CertCheck certInfobyId(String fileid);
    String username(String fileid);

}
