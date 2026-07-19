package com.dormitory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dormitory.entity.Room;
import org.apache.ibatis.annotations.Mapper;

/**
 * 宿舍房间Mapper接口
 */
@Mapper
public interface RoomMapper extends BaseMapper<Room> {
}
