package io.smarthealth.infrastructure.utility;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import io.smarthealth.administration.app.domain.Address.Type;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.type.LongType;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author Simon.waweru
 */
public class DatePrefixedSequenceIdGenerator extends SequenceStyleGenerator {

    public static final String DATE_FORMAT_PARAMETER = "dateFormat";
    public static final String DATE_FORMAT_DEFAULT = "%tY-%tm";

    public static final String NUMBER_FORMAT_PARAMETER = "numberFormat";
    public static final String NUMBER_FORMAT_DEFAULT = "%05d";

    public static final String DATE_NUMBER_SEPARATOR_PARAMETER = "dateNumberSeparator";
    public static final String DATE_NUMBER_SEPARATOR_DEFAULT = "_";

    private String format;

    @Override
    public Serializable generate(SharedSessionContractImplementor session,
            Object object) throws HibernateException {
        return String.format(format, LocalDate.now(), super.generate(session, object));
    }
    //Type type, Properties params, ServiceRegistry serviceRegistry

//    @Override
    public void configure(Type type, Properties params,
            ServiceRegistry serviceRegistry) {
        super.configure(LongType.INSTANCE, params, serviceRegistry);

        String dateFormat = ConfigurationHelper.getString(DATE_FORMAT_PARAMETER, params, DATE_FORMAT_DEFAULT).replace("%", "%1$");
        String numberFormat = ConfigurationHelper.getString(NUMBER_FORMAT_PARAMETER, params, NUMBER_FORMAT_DEFAULT).replace("%", "%2$");
        String dateNumberSeparator = ConfigurationHelper.getString(DATE_NUMBER_SEPARATOR_PARAMETER, params, DATE_NUMBER_SEPARATOR_DEFAULT);
        this.format = dateFormat + dateNumberSeparator + numberFormat;
    }
}
