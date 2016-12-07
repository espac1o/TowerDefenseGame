using UnityEngine;
using System.Collections;

public class SpawnerScript : MonoBehaviour {

    public float calm_time = 15;
    private float curr_calm_time = 0;
    public float spawn_rate = 1;
    private float curr_spawn_time = 0;

    public int[] Wave_len = { 2 };

    public GameObject target;
    public GameObject[] monsters;

    public float speed = 4;
    public float hp = 10;
    public float damage = 1f;

    private bool is_Wave = false;
    private int curr_count = 0;


    public ArrayList monsters_list = new ArrayList();

	// Use this for initialization
	void Start () {
        curr_calm_time = 3;
		curr_spawn_time = spawn_rate;
        target = null;
	}
	
	// Update is called once per frame
	void FixedUpdate () {
        if (!is_Wave)
        {
            if (curr_calm_time > 0)
            {
                curr_calm_time -= Time.deltaTime;
            }
            else
            {
                Wave(Wave_len[0]);
                curr_calm_time = calm_time;
            }
        }

        if (curr_count > 0)
        {
            if (curr_spawn_time > 0)
            {
                curr_spawn_time -= Time.deltaTime;
            }
            else
            {
                spawn(0);
                curr_spawn_time = spawn_rate;
                curr_count -= 1;
                if (curr_count <= 0) { is_Wave = false; }
            }
        }
        
	}

    void Wave(int count)
    {
        is_Wave = true;
        curr_count = count;
    }

    void spawn(int monster_type)
    {
        var monst_new = Instantiate(monsters[monster_type]) as GameObject;
        monsters_list.Add(monst_new);

        monst_new.GetComponent<Transform>().position = gameObject.GetComponent<Transform>().position;
        monst_new.GetComponent<UnitScript>().speed = speed;
        monst_new.GetComponent<UnitScript>().damage = damage;
        monst_new.GetComponent<UnitScript>().hp = hp;
    }
}
