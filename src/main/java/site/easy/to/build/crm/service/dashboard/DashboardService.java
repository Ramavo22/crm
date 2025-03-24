package site.easy.to.build.crm.service.dashboard;

import site.easy.to.build.crm.entity.Lead;

import java.util.Map;

public interface DashboardService {

    public Map<String,Integer>getCustomerCountPerMonth(int year);

    public Map<String,Integer>getLeadCountPerMonth(int year);

    public Map<String,Integer>getTicketCountPerYear(int year);

    
}
