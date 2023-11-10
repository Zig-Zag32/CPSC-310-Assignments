package edu.trinity.got;

import edu.trinity.functional.SummaryStatistics;

import java.util.*;

public class InMemoryMemberDAO implements MemberDAO {
    private final Collection<Member> allMembers =
            MemberDB.getInstance().getAllMembers();

    @Override
    public Optional<Member> findById(Long id) {
        return allMembers.stream()
                .filter(member -> member.id().equals(id))
                .findAny();
    }

    @Override
    public Optional<Member> findByName(String name) {
        return allMembers.stream()
                .filter(member -> member.name().equals(name))
                .findAny();
    }

    @Override
    public List<Member> findAllByHouse(House house) {
        return allMembers.stream()
                .filter(member -> member.house().equals(house))
                .toList();
    }

    @Override
    public Collection<Member> getAll() {
        return allMembers.stream()
                .toList();
    }

    /**
     * Find all members whose name starts with S and sort by id (natural sort)
     */
    @Override
    public List<Member> startWithSandSortAlphabetically() {
        return allMembers.stream()
                .filter(member -> member.name().startsWith("S"))
                .sorted(Comparator.comparingLong(Member::id))
                .toList();
    }

    /**
     * Final all Lannisters and sort them by name
     */
    @Override
    public List<Member> lannisters_alphabeticallyByName() {
        return allMembers.stream()
                .filter(member -> member.house().equals(House.LANNISTER))
                .sorted(Comparator.comparing(Member::name))
                .toList();
    }

    /**
     * Find all members whose salary is less than the given value and sort by house
     */
    @Override
    public List<Member> salaryLessThanAndSortByHouse(double max) {
        return allMembers.stream()
                .filter(member -> member.salary() < max)
                .sorted(Comparator.comparing(Member::house))
                .toList();
    }

    /**
     * Sort members by House, then by name
     */
    @Override
    public List<Member> sortByHouseNameThenSortByNameDesc() {
        return allMembers.stream()
                .sorted(Comparator.comparing(Member::house))
                .sorted(Comparator.comparing(Member::name))
                .toList();
    }

    /**
     * Sort the members of a given House by birthdate
     */
    @Override
    public List<Member> houseByDob(House house) {
        return allMembers.stream()
                .filter(member -> member.house().equals(house))
                .sorted(Comparator.comparing(Member::dob))
                .toList();
    }

    /**
     * Find all Kings and sort by name in descending order
     */
    @Override
    public List<Member> kingsByNameDesc() {
        return allMembers.stream()
                .filter(member -> member.title().equals(Title.KING))
                .sorted(Comparator.comparing(Member::name).reversed())
                .toList();
    }

    /**
     * Find the average salary of all the members
     */
    @Override
    public double averageSalary() {
        DoubleSummaryStatistics stats = allMembers.stream()
                .mapToDouble(Member::salary)
                .summaryStatistics();
        return stats.getAverage();
    }

    /**
     * Get the names of a given house, sorted in natural order
     * (note sort by _names_, not members)
     */
    @Override
    public List<String> namesSorted(House house) {
        return allMembers.stream()
                .filter(member -> member.house().equals(house))
                .map(Member::name)
                .sorted()
                .toList();
    }

    /**
     * Are any of the salaries greater than 100K?
     */
    @Override
    public boolean salariesGreaterThan(double max) {
        DoubleSummaryStatistics stats = allMembers.stream()
                .mapToDouble(Member::salary)
                .summaryStatistics();
        return stats.getMax() > max;
    }

    /**
     * Are there any members of given house?
     */
    @Override
    public boolean anyMembers(House house) {
        List<Member> members = allMembers.stream()
                .filter(member -> member.house().equals(house))
                .toList();
        return !members.isEmpty();
    }

    /**
     * How many members of a given house are there?
     */
    @Override
    public long howMany(House house) {
        List<Member> members = allMembers.stream()
                .filter(member -> member.house().equals(house))
                .toList();
        return members.size();
    }

    /**
     * What is the average name length of the members of a given house?
     */
    @Override
    public double avgNameLenOfHouse(House house) {
        return allMembers.stream()
                .filter(member -> member.house().equals(house))
                .mapToInt(member -> member.name().length())
                .summaryStatistics()
                .getAverage();
    }

    /**
     * Return the names of a given house as a comma-separated string
     */
    @Override
    public String houseMemberNames(House house) {
        List<Member> members = findAllByHouse(house);
        StringBuilder names = new StringBuilder();
        members.forEach(w -> names.append(w.name()).append(", "));
        names.setLength(names.length() - 2);
        return names.toString();

    }

    /**
     * Who has the highest salary?
     */
    @Override
    public Optional<Member> highestSalary() {
        return allMembers.stream()
                .max(Comparator.comparingDouble(Member::salary));
    }

    /**
     * Partition members into royalty and non-royalty
     * (note: royalty are KINGs and QUEENs only)
     */
    @Override
    public Map<Boolean, List<Member>> royaltyPartition() {
        Map<Boolean, List<Member>> partition = new HashMap<>();
        partition.put(true, allMembers.stream()
                .filter(member -> member.title().equals(Title.KING) || member.title().equals(Title.QUEEN))
                .toList());
        partition.put(false, allMembers.stream()
                .filter(member -> !(member.title().equals(Title.KING) || member.title().equals(Title.QUEEN)))
                .toList());
        return partition;
    }

    /**
     * Group members into Houses
     */
    @Override
    public Map<House, List<Member>> membersByHouse() {
        Map<House, List<Member>> houses = new HashMap<>();
        Arrays.stream(House.values()).toList().stream()
                .forEach(house -> houses.put(house, allMembers.stream()
                        .filter(member -> member.house().equals(house))
                        .toList()));
        return houses;
    }

    /**
     * How many members are in each house?
     * (group by house, downstream collector using counting
     */
    @Override
    public Map<House, Long> numberOfMembersByHouse() {
        Map<House, Long> membersPerHouse = new HashMap<>();
        Arrays.stream(House.values()).toList().stream()
                .forEach(house -> membersPerHouse.put(house, (long) allMembers.stream()
                        .filter(member -> member.house().equals(house))
                        .toList()
                        .size()));
        return membersPerHouse;
    }

    /**
     * Get the max, min, and ave salary for each house
     */
    @Override
    public Map<House, DoubleSummaryStatistics> houseStats() {
        Map<House, DoubleSummaryStatistics> houseStats = new HashMap<>();
        Arrays.stream(House.values()).toList().stream()
                .forEach(house -> houseStats.put(house, allMembers.stream()
                        .filter(member -> member.house().equals(house))
                        .mapToDouble(Member::salary)
                        .summaryStatistics()));
        return houseStats;
    }

}
