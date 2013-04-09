package models;

import java.util.*;
import play.data.validation.Constraints.*;

/**
 * Created with IntelliJ IDEA.
 * User: fredrik hansen
 * Date: 09.04.13
 * Time: 21:14
 * To change this template use File | Settings | File Templates.
 */
public class Task {
    public Long id;

    @Required
    public String label;

    public static List<Task> all() {
        return new ArrayList<Task>();
    }

    public static void create(Task task) {
    }

    public static void delete(Long id) {
    }
}
