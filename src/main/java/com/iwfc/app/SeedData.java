package com.iwfc.app;

import com.iwfc.exceptions.DuplicateDataException;
import com.iwfc.exceptions.UnauthorizedAccessException;
import com.iwfc.facade.IWFCFacade;
import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentType;
import com.iwfc.model.Session;
import com.iwfc.model.Zone;
import com.iwfc.model.session.HiitSession;
import com.iwfc.model.session.YogaSession;
import com.iwfc.factory.EquipmentFactory;
import com.iwfc.repository.DataStore;
import com.iwfc.users.Administrator;
import com.iwfc.users.Instructor;
import com.iwfc.users.Member;
import com.iwfc.users.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

final class SeedData {

    private SeedData() {
    }

    static List<User> load(DataStore dataStore, IWFCFacade facade)
            throws DuplicateDataException, UnauthorizedAccessException {
        Administrator admin = new Administrator("U-ADMIN", "Alice Admin", "alice@iwfc.local");
        Instructor instructor = new Instructor("U-INSTR", "Ian Instructor", "ian@iwfc.local");
        Member member = new Member("U-MEMBER", "Mona Member", "mona@iwfc.local");

        dataStore.getUserRepository().add(admin);
        dataStore.getUserRepository().add(instructor);
        dataStore.getUserRepository().add(member);

        Equipment treadmill = EquipmentFactory.createEquipment(EquipmentType.TREADMILL, "EQ-001", Zone.CARDIO_ZONE);
        Equipment spinBike = EquipmentFactory.createEquipment(EquipmentType.SPIN_BIKE, "EQ-002", Zone.SPIN_STUDIO);
        facade.registerEquipment(admin, treadmill);
        facade.registerEquipment(admin, spinBike);

        Session hiit = new HiitSession("SS-001", instructor, Zone.STUDIO_A,
                nextWeekdayAt(9, 0), 45);
        Session yoga = new YogaSession("SS-002", instructor, Zone.STUDIO_B,
                nextWeekdayAt(18, 0), 60);
        facade.scheduleSession(instructor, hiit);
        facade.scheduleSession(instructor, yoga);

        return Arrays.asList(admin, instructor, member);
    }

    private static LocalDateTime nextWeekdayAt(int hour, int minute) {
        return LocalDateTime.now().plusDays(1).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
    }
}
