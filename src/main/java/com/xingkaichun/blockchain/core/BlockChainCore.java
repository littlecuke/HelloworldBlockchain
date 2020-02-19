package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.listen.BlockChainActionData;
import com.xingkaichun.blockchain.core.listen.BlockChainActionListener;
import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.model.enums.BlockChainActionEnum;

import java.util.ArrayList;
import java.util.List;

public class BlockChainCore {

    private Miner miner ;
    private Synchronizer synchronizer;
    //监听区块链上区块的增删动作
    private List<BlockChainActionListener> blockChainActionListenerList = new ArrayList<>();

    public BlockChainCore(Miner miner,Synchronizer synchronizer) throws Exception {
        this.miner = miner;
        this.synchronizer = synchronizer;
    }

    /**
     * 启动
     * 这里是一个单线程实现。为了协调节点间的区块同步、矿工的挖矿，先进行节点间区块数据的同步，
     * 同步结束后，矿工进行一段时间的挖矿，然后退出挖矿，进行区块同步，矿工进行一段时间的挖矿，
     * 然后退出挖矿，进行区块同步......
     */
    public void run() throws Exception {
        while (isActive()){
            synchronizer.run();
            miner.mine();
        }
    }

    /**
     * 暂停所有
     */
    public void pause() throws Exception {
        synchronizer.pause();
        miner.pauseMine();
    }

    /**
     * 恢复所有
     */
    public void resume() throws Exception {
        synchronizer.resume();
        miner.resumeMine();
    }

    public boolean isActive() throws Exception {
        return synchronizer.isActive() || miner.isActive();
    }


    //region 监听器
    public void registerBlockChainActionListener(BlockChainActionListener blockChainActionListener){
        blockChainActionListenerList.add(blockChainActionListener);
    }

    public void notifyBlockChainActionListener(List<BlockChainActionData> dataList) {
        for (BlockChainActionListener listener: blockChainActionListenerList) {
            listener.addOrDeleteBlock(dataList);
        }
    }

    public List<BlockChainActionData> createBlockChainActionDataList(Block block, BlockChainActionEnum blockChainActionEnum) {
        List<BlockChainActionData> dataList = new ArrayList<>();
        BlockChainActionData addData = new BlockChainActionData(block,blockChainActionEnum);
        dataList.add(addData);
        return dataList;
    }

    public List<BlockChainActionData> createBlockChainActionDataList(List<Block> firstBlockList, BlockChainActionEnum firstBlockChainActionEnum, List<Block> nextBlockList, BlockChainActionEnum nextBlockChainActionEnum) {
        List<BlockChainActionData> dataList = new ArrayList<>();
        BlockChainActionData deleteData = new BlockChainActionData(firstBlockList,firstBlockChainActionEnum);
        dataList.add(deleteData);
        BlockChainActionData addData = new BlockChainActionData(nextBlockList,nextBlockChainActionEnum);
        dataList.add(addData);
        return dataList;
    }
    //endregion
}