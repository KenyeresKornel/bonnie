package com.cgi.bonnie.storage.order;

import com.cgi.bonnie.businessrules.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<AssemblyOrder, Long> {

    public List<AssemblyOrder> findAll();
    public List<AssemblyOrder> findAllByAssignedTo(Long assembler);
    public List<AssemblyOrder> findAllByShopOrderId( String shopOrderId);

    public List<AssemblyOrder> findAllByStatus(Status status);
}
