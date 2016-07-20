

package lambdas.lesson2.exercise;

import lambdas.lesson1.Person;
import lambdas.lesson2.example.Employee;
import lambdas.lesson2.example.JobHistoryEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class Mapping {

    private static class MapHelper<T> {
        private final List<T> list;

        public MapHelper(List<T> list) {
            this.list = list;
        }

        public List<T> getList() {
            return list;
        }

        public <R> MapHelper<R> map(Function<T, R> f) {
            // TODO
            final List<R> result = new ArrayList<R>();
            for (T t : list) {
                result.add(f.apply(t));
            }
            return new MapHelper<R>(result);
        }

        // TODO *
        // public <R> MapHelper<R> flatMap(Function<T, List<R>> f)
    }

    @Test
    public void mapping() {
        final List<Employee> employees =
                Arrays.asList(
                        new Employee(
                                new Person("a", "Galt"),
                                Arrays.asList(
                                        new JobHistoryEntry(2, "dev", "epam"),
                                        new JobHistoryEntry(1, "dev", "google")
                                )),
                        new Employee(
                                new Person("b", "Doe"),
                                Arrays.asList(
                                        new JobHistoryEntry(3, "qa", "yandex"),
                                        new JobHistoryEntry(1, "qa", "epam"),
                                        new JobHistoryEntry(1, "dev", "abc")
                                )),
                        new Employee(
                                new Person("c", "White"),
                                Collections.singletonList(
                                        new JobHistoryEntry(5, "qa", "epam")
                                ))
                );

        final List<Employee> mappedEmployees =
                new MapHelper<>(employees)

                        //TODO for test mapping()
                        .map(e -> e.withPerson(e.getPerson().withFirstName("John"))) // change name to John
                        .map(e -> e.withJobHistory(addOneYear(e.getJobHistory()))) // add 1 year to experience duration
                        .map(e -> e.withJobHistory(replaceQA(e.getJobHistory()))) // replace qa with QA

                        .getList();

        final List<Employee> expectedResult =
                Arrays.asList(
                        new Employee(
                                new Person("John", "Galt"),
                                Arrays.asList(
                                        new JobHistoryEntry(3, "dev", "epam"),
                                        new JobHistoryEntry(2, "dev", "google")
                                )),
                        new Employee(
                                new Person("John", "Doe"),
                                Arrays.asList(
                                        new JobHistoryEntry(4, "QA", "yandex"),
                                        new JobHistoryEntry(2, "QA", "epam"),
                                        new JobHistoryEntry(2, "dev", "abc")
                                )),
                        new Employee(
                                new Person("John", "White"),
                                Collections.singletonList(
                                        new JobHistoryEntry(6, "QA", "epam")
                                ))
                );

        assertEquals(mappedEmployees, expectedResult);
    }
    //TODO for test mapping()
    private List<JobHistoryEntry> replaceQA(List<JobHistoryEntry> jobHistory) {
        return new MapHelper<>(jobHistory)
                .map(jhe -> {

                    if (jhe.getPosition().equals("qa")) {
                        return jhe.withPosition("QA");
                    } else {
                        return jhe.withPosition(jhe.getPosition());
                    }

                })
                .getList();
    }
    //TODO for test mapping()
    private List<JobHistoryEntry> addOneYear(List<JobHistoryEntry> jobHistory) {
        return new MapHelper<>(jobHistory)
                .map(jhe -> jhe.withDuration(jhe.getDuration()+1))
                .getList();
    }

    //TODO for test lazy_mapping()
    private static class LazyMapHelperNext<T, R> {
        private final List<T> list;
        private final Function<T, R> function;

        public LazyMapHelperNext(List<T> list, Function<T, R> function) {
            this.list = list;
            this.function = function;
        }

        public List<R> force() {
            final List<R> result = new ArrayList<R>();
            for (T t : list) {
                result.add(function.apply(t));
            }
            return result;
        }

        // T -> R, R -> R2
        // T -> R2

        public <R2> LazyMapHelperNext<T, R2> map(Function<R, R2> f) {
            Function<T, R2> function = (T t) -> {
                R apply = this.function.apply(t);
                final R2 apply1 = f.apply(apply);
                return apply1;

            };
            return new LazyMapHelperNext(list, function);
        }

        // TODO *
        // public <R> LazyMapHelper<R> flatMap(Function<T, List<R>> f)
    }
    //TODO for test lazy_mapping()
    private static class LazyMapHelper<T> {

        private final List<T> list;

        public LazyMapHelper(List<T> list) {
            this.list = list;
        }

        public List<T> force() {
            return list;
        }

        public <R> LazyMapHelperNext<T, R> map(Function<T, R> f) {
            return new LazyMapHelperNext<T, R>(list, f);
        }

        // TODO *
        // public <R> LazyMapHelper<R> flatMap(Function<T, List<R>> f)
    }

    @Test
    public void lazy_mapping() {
        final List<Employee> employees =
                Arrays.asList(
                        new Employee(
                                new Person("a", "Galt"),
                                Arrays.asList(
                                        new JobHistoryEntry(2, "dev", "epam"),
                                        new JobHistoryEntry(1, "dev", "google")
                                )),
                        new Employee(
                                new Person("b", "Doe"),
                                Arrays.asList(
                                        new JobHistoryEntry(3, "qa", "yandex"),
                                        new JobHistoryEntry(1, "qa", "epam"),
                                        new JobHistoryEntry(1, "dev", "abc")
                                )),
                        new Employee(
                                new Person("c", "White"),
                                Collections.singletonList(
                                        new JobHistoryEntry(5, "qa", "epam")
                                ))
                );

        final List<Employee> mappedEmployees =
                new LazyMapHelper<>(employees)
                        //TODO for test lazy_mapping()
                        .map(e -> e.withPerson(e.getPerson().withFirstName("John")))// change name to John
                        .map(e -> e.withJobHistory(addOneYear(e.getJobHistory()))) // add 1 year to experience duration
                        .map(e -> e.withJobHistory(replaceQA(e.getJobHistory()))) // replace qa with QA
                        .force();

        final List<Employee> expectedResult =
                Arrays.asList(
                        new Employee(
                                new Person("John", "Galt"),
                                Arrays.asList(
                                        new JobHistoryEntry(3, "dev", "epam"),
                                        new JobHistoryEntry(2, "dev", "google")
                                )),
                        new Employee(
                                new Person("John", "Doe"),
                                Arrays.asList(
                                        new JobHistoryEntry(4, "QA", "yandex"),
                                        new JobHistoryEntry(2, "QA", "epam"),
                                        new JobHistoryEntry(2, "dev", "abc")
                                )),
                        new Employee(
                                new Person("John", "White"),
                                Collections.singletonList(
                                        new JobHistoryEntry(6, "QA", "epam")
                                ))
                );

        assertEquals(mappedEmployees, expectedResult);
    }
}
