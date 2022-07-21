package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.model.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findByName(String name, Class... clazz);


}
