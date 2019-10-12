package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.School;
import com.github.adminfaces.starter.service.SchoolService;
import com.github.adminfaces.starter.util.Db;
import static com.github.adminfaces.starter.util.Db.ps;
import static com.github.adminfaces.starter.util.Db.rs;
import com.github.adminfaces.template.exception.BusinessException;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * Created by rmpestano on 12/02/17.
 */

@Named
@ViewScoped
public class SchoolsListMB implements Serializable {
private static Logger logger = Logger.getLogger(SchoolsListMB.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    SchoolService schoolService;

    String opno;

    LazyDataModel<School> patients;

    Filter<School> filter = new Filter<>(new School());

    List<School> selectedPatients; //patients selected in checkbox column

    List<School> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        patients = new LazyDataModel<School>() {
            @Override
            public List<School> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                com.github.adminfaces.starter.infra.model.SortOrder order = null;
                if (sortOrder != null) {
                    order = sortOrder.equals(SortOrder.ASCENDING) ? com.github.adminfaces.starter.infra.model.SortOrder.ASCENDING
                            : sortOrder.equals(SortOrder.DESCENDING) ? com.github.adminfaces.starter.infra.model.SortOrder.DESCENDING
                            : com.github.adminfaces.starter.infra.model.SortOrder.UNSORTED;
                }
                filter.setFirst(first).setPageSize(pageSize)
                        .setSortField(sortField).setSortOrder(order)
                        .setParams(filters);
                List<School> list = schoolService.paginate(filter);
                setRowCount((int) schoolService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public School getRowData(String key) {
                return schoolService.findByOpno(key);
            }
        };
    }

    public void clear() {
        filter = new Filter<School>(new School());
    }

    public List<String> completeModelOpno(String query) {
        List<String> result = schoolService.getOpno(query);
        return result;
    }

    public List<String> completeModel(String query) {
        List<String> result = schoolService.getNames(query);
        return result;
    }

    public List<String> completeModelPhone(String query) {
        List<String> result = schoolService.getPhones(query);
        return result;
    }
    
    public List<String> completeModelEcde(String query) {
        Db.connect();
        List<String> data = new ArrayList<>();
        try {
            ps = Db.conn.prepareStatement("SELECT names FROM tbl_temis_ecde_centres where names like '%"+query+"%'");
            rs = ps.executeQuery();
            while(rs.next()){
                data.add(rs.getString("names"));
            }               
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        Db.disconnect();
        return data;
    }
       
    public void findPatientById(String opno) {
        if (opno == null) {
            throw new BusinessException("Provide School ID to load");
        }
        selectedPatients.add(schoolService.findByOpno(opno));
    }

    public void delete() {
        int numPatients = 0;
        for (School selectedCar : selectedPatients) {
            numPatients++;
            schoolService.remove(selectedCar);
        }
        selectedPatients.clear();
        addDetailMessage(numPatients + " School deleted successfully!");
    }

    public List<School> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<School> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public LazyDataModel<School> getPatients() {
        return patients;
    }

    public void setPatients(LazyDataModel<School> patients) {
        this.patients = patients;
    }

    public Filter<School> getFilter() {
        return filter;
    }

    public void setFilter(Filter<School> filter) {
        this.filter = filter;
    }

    public List<School> getSelectedPatients() {
        return selectedPatients;
    }

    public void setSelectedPatients(List<School> selectedPatients) {
        this.selectedPatients = selectedPatients;
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }
}
